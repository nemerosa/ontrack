import subprocess


def run_command(executable, args):
    arguments = [executable]
    arguments = arguments + args
    return subprocess.check_output(arguments)
