import sys
import json
import io
import traceback
import builtins

# ─── Output capture ───────────────────────────────────────────────────────────
captured_output = []
original_print = builtins.print

def capturing_print(*args, sep=' ', end='\n', **kwargs):
    text = sep.join(str(a) for a in args) + end
    captured_output.append(text)
    # Don't call original_print - we capture it instead

builtins.print = capturing_print

# ─── Object heap ──────────────────────────────────────────────────────────────
def build_object_heap(local_vars, global_vars):
    heap = {}
    all_vars = {**global_vars, **local_vars}
    for val in all_vars.values():
        if hasattr(val, '__dict__') and not isinstance(val, type):
            obj_id = f"obj_{id(val)}"
            attrs = {}
            for k, v in val.__dict__.items():
                if not k.startswith('_'):
                    attrs[k] = serialize_value(v)
            heap[obj_id] = {
                "type": type(val).__name__,
                "attrs": attrs
            }
    return heap if heap else None

# ─── Value serializer ─────────────────────────────────────────────────────────
def serialize_value(val):
    if val is None or isinstance(val, (bool, int, float, str)):
        return val
    if isinstance(val, (list, tuple)):
        return [serialize_value(i) for i in val]
    if isinstance(val, dict):
        return {str(k): serialize_value(v) for k, v in val.items()}
    if isinstance(val, set):
        return list(val)
    if hasattr(val, '__dict__') and not isinstance(val, type):
        return f"<{type(val).__name__} obj_{id(val)}>"
    return str(val)

# ─── Variable filter ──────────────────────────────────────────────────────────
SKIP_VARS = {
    '__name__', '__doc__', '__package__', '__loader__', '__spec__',
    '__builtins__', '__file__', '__cached__', '__annotations__',
    'capturing_print', 'original_print', 'captured_output'
}

def filter_vars(vars_dict):
    return {
        k: serialize_value(v)
        for k, v in vars_dict.items()
        if not k.startswith('__') and k not in SKIP_VARS and not callable(v)
    }

# ─── Tracer ───────────────────────────────────────────────────────────────────
steps = []
MAX_STEPS = 500
user_filename = '<string>'

def tracer(frame, event, arg):
    if len(steps) >= MAX_STEPS:
        return None

    # Only trace user code, skip stdlib/internals
    if frame.f_code.co_filename != user_filename:
        return tracer

    if event not in ('line', 'call', 'return', 'exception'):
        return tracer

    local_vars = filter_vars(frame.f_locals)
    global_vars = filter_vars(frame.f_globals)
    objects = build_object_heap(frame.f_locals, frame.f_globals)

    # Capture output produced since last step
    output = None
    if captured_output:
        output = ''.join(captured_output)
        captured_output.clear()

    step = {
        "event": event,
        "frame": {
            "line": frame.f_lineno,
            "function": frame.f_code.co_name,
            "locals": local_vars,
            "globals": global_vars
        },
        "output": output
    }

    if objects:
        step["objects"] = objects

    if event == 'exception' and arg is not None:
        exc_type, exc_val, _ = arg
        step["exception"] = f"{exc_type.__name__}: {exc_val}"

    steps.append(step)
    return tracer

# ─── Main ─────────────────────────────────────────────────────────────────────
def run_trace(code, stdin_data=""):
    global steps, captured_output
    steps = []
    captured_output = []

    if stdin_data:
        sys.stdin = io.StringIO(stdin_data)

    try:
        compiled = compile(code, user_filename, 'exec')
        sys.settrace(tracer)
        exec(compiled, {})
    except Exception:
        # Capture any remaining output
        output = None
        if captured_output:
            output = ''.join(captured_output)
            captured_output.clear()

        steps.append({
            "event": "error",
            "frame": None,
            "output": output,
            "exception": traceback.format_exc()
        })
    finally:
        sys.settrace(None)
        sys.stdin = sys.__stdin__

    return {
        "steps": steps,
        "totalSteps": len(steps),
        "truncated": len(steps) >= MAX_STEPS
    }

if __name__ == '__main__':
    import base64
    code_b64 = sys.argv[1]
    stdin_b64 = sys.argv[2] if len(sys.argv) > 2 else ''

    code = base64.b64decode(code_b64).decode('utf-8')
    stdin = base64.b64decode(stdin_b64).decode('utf-8') if stdin_b64 else ''

    result = run_trace(code, stdin)
    builtins.print = original_print
    sys.stdout.write(json.dumps(result))
    sys.stdout.flush()