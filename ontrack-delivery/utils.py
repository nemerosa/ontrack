import subprocess


def run_command(executable, args):
    arguments = [executable]
    arguments = arguments + args
    output = subprocess.check_output(arguments)
    print output
    return output
