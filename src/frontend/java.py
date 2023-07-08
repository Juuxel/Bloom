import json
from pathlib import Path
from queue import Queue, Empty
from subprocess import Popen, PIPE
from threading import Thread


class BackendHandle:
    def __init__(self, process: Popen):
        self.__process = process
        stdout_queue = Queue()
        self.__stdout_queue = stdout_queue
        stdout_thread = Thread(target=poll_responses, args=(process.stdout, stdout_queue), daemon=True)
        stdout_thread.start()

    def poll_response(self):
        try:
            line = self.__stdout_queue.get_nowait()
            return json.loads(line)
        except Empty:
            return {}

    def send_message(self, message):
        encoded = json.dumps(message)
        self.__process.stdin.write(encoded + "\n")
        self.__process.stdin.flush()

    def exit(self):
        self.send_message({"type": "exit"})


def poll_responses(stdout, queue):
    for line in stdout:
        queue.put(line)


def find_java_executable():
    return "java"  # from PATH


def start_backend():
    directory = Path(__file__).parent
    backend = directory / "backend.jar"
    process = Popen([find_java_executable(), "-jar", str(backend.absolute())],
                    text=True, stdin=PIPE, stdout=PIPE)
    return BackendHandle(process)
