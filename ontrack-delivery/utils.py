import subprocess


def run_command(executable, args):
    arguments = [executable]
    arguments = arguments + args
    co_code = subprocess.call(arguments)
    if co_code != 0:
        raise Exception("Cannot run command %s %s: %d" % (executable, args, co_code))
