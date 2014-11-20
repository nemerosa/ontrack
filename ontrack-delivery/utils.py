import os


def run_command(executable, args):
    co_code = os.spawnv(os.P_WAIT, executable, args)
    if co_code != 0:
        raise Exception("Cannot run command %s %s: %d" % (executable, args, co_code))
