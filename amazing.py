#!/usr/bin/env python3

import os
from shutil import which
import signal
import subprocess
import sys
import time


# subprocesses started
# (global variables so that handler can kill them later)
java = 0
server = 0
yarn = 0
gradle = 0
firefox = 0
version = "0.5"

# allowed arguments for script
arguments = {
    "-n" : "initial project build / build project new",
    "--new" : "initial project build / build project new",
    "--debug" : "enables debug mode",
    "--dry" : "dry run",
    "-h" : "print help",
    "--help" : "print help",
    "-p" : "(-p <int>) set server port to <int>",
    "--port" : "(--port <int>) set server port to <int>",
    "--remote" : "enables remote mode",
    "-v" : "print version",
    "--verbose" : "print version"
}


# terminate subprocesses, then exit
def error(exit_code, msg):
    global gradle
    global server
    global java
    global yarn
    global firefox

    print(msg, file=sys.stderr)

    # try to terminate subprocesses with SIGTERM
    subprocess.call(["gradle", "--stop"])  # stops gradle deamon
    if gradle != 0:
        gradle.terminate()
        print("[STOP] gradle stopped")

    if server != 0:
        server.terminate()
        print("[STOP] server stopped")

    if java != 0:
        java.terminate()
        print("[STOP] java stopped")

    if yarn != 0:
        yarn.terminate()
        print("[STOP] yarn stopped")

    if firefox != 0:
        firefox.terminate()
        print("[STOP] firefox stopped")

    # otherwise try to kill subprocesses with SIGKILL
    if gradle != 0 and not gradle.poll():
        print("[KILL] gradle not terminating")
        gradle.kill()

    if server != 0 and not server.poll():
        print("[KILL] server not terminating")
        java.kill()

    if java != 0 and not java.poll():
        print("[KILL] java not terminating")
        java.kill()

    if yarn != 0 and not yarn.poll():
        print("[KILL] server not terminating")
        yarn.kill()

    if firefox != 0 and not firefox.poll():
        print("[KILL] server not terminating")
        firefox.kill()

    print("[SIGINT] Background processes killed. Terminating now.")
    exit(exit_code)


# handle SIGINT and SIGKILL
def handler(signal, frame):
    error(0, "\n[SIGINT] SIGINT detected. Killing background processes ...")



def assertSuccess(ret, msg):
    if ret:
        error(1, f"[ERROR] {msg}")

# print help message
# if error is given, wrong argument given -> print error message
def print_help(error):
    global arguments

    msg = ""
    for arg in arguments:
        msg += f"{arg}\t: {arguments[arg]} \n"

    if error:
        msg = f"UNKNOWN ARGUMENT GIVEN: {error}\n{msg}"
        print(msg, file=sys.stderr)
        exit(1)
    else:
        print(msg)
        exit(0)


# parse user's arguments
def parse_arguments():
    global arguments

    given = ""
    new = False

    for i in range(1, len(sys.argv)):
        arg = sys.argv[i]
        if arg in arguments:
            
            if arg == "-h" or arg == "--help":
                print_help("") # print help
            elif arg == "-n" or "--new":
               new = True
            elif arg == "-p" or "--port":
                # next argument has to be number
                i = i+1
                if not (i < len(sys.argv) and arg[i].isalnum()):
                    print_help("port needs integer")
            else:
                given += arg + " "

        else:
            print_help(arg) # error with argument, print help with error

    return new, given


# check if all dependencies are installed
def check_dependencies():
    if which("java") is None:
        error(1, "[ERROR] java not installed!")
    if which("gradle") is None:
        error(1, "[ERROR] gradle not installed!")
    if which("pip3") is None:
        error(1, "[ERROR] pip3 is not installed!")
    if which("yarn") is None:
        error(1, "[ERROR] yarn is not installed!")


# get host name and ports for remote execution
def remote():
    print("Please enter host name:")
    while True:
        host = input()
        if not str(host).isalpha():
            print("Every char of host name has to be in the alphabet. Please enter a valid name.")
        else:
            break
    print("Please enter port for client:")
    while True:
        c_port = input()
        if not str(c_port).isalnum():
            print("Client port has to be a number. Please enter a valid port.")
        else:
            break
    print("Please enter port for server:")
    while True:
        s_port = input()
        if not str(c_port).isalnum():
            print("Server port has to be a number. Please enter a valid port.")
        else:
            break
    return str(host), str(c_port), str(s_port)


# check if execution on localhost is wanted
def localhost():
    true = ["yes", "ye", "y", "", "\n", "\n\r"]
    false =["no", "n"]
    while True:
        print("Do you want to run locally (on localhost)? [Yes / no]", end=" ")
        choice = input().lower()
        if choice in true:
             return "localhost", "8080", "8081"
        elif choice in false:
            return remote()
        else:
            print("Please respond with 'yes' or 'no'.")


def main():
    global gradle
    global server
    global java
    global yarn
    global firefox

    check_dependencies() # check if requirements are installed

    new, args = parse_arguments()
    host, client_port, java_port = localhost() # check which ports to use (localhost or remote)
    cwd = os.getcwd()

    # run gradle
    if new:
        subprocess.call(["rm", "-rf", f"{cwd}/server/libs"],
                        stdout=subprocess.DEVNULL)
        
    os.chdir(f"{cwd}/server")
    if new:
        print("[START] gradle build started")
        assertSuccess(subprocess.call(["gradle", "wrapper"],
                        stdout=subprocess.DEVNULL), "Could not build gradle wrapper!")

    gradle = subprocess.Popen("./gradlew jar",
             stdout=subprocess.DEVNULL,
             shell=True)

    # build yarn
    if new:
        print("[START] yarn install started")
        os.chdir(f"{cwd}/client")
        assertSuccess(subprocess.call(["yarn", "install"],
                        stdout=subprocess.DEVNULL), "Could not execute yarn!")

    # load tasks and run server
    os.chdir(f"{cwd}/admin")
    venv = f"{cwd}/admin/venv"
    
    print("[START] pip3 install started")
    assertSuccess(subprocess.call(f"python3 -m venv {venv}",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not build python virtual environment!")
    assertSuccess(which(f"{venv}/bin/python3") is None, "Python virtual environment not build!")
    
    assertSuccess(subprocess.call(f"{venv}/bin/pip3 install -r requirements.txt",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not install at least one of the python requirements!")

    assertSuccess(subprocess.call(f"{venv}/bin/pip3 freeze",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not freeze virtual env!")
    print("[START] python3 venv started")

    assertSuccess(subprocess.call(f"{venv}/bin/python3 manage.py migrate admin zero",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not load tasks!")
    assertSuccess(subprocess.call(f"{venv}/bin/python3 manage.py migrate",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not migrate tasks!")
    assertSuccess(subprocess.call(f"{venv}/bin/python3 manage.py loaddata admin/fixtures/fixture.yaml",
                    stdout=subprocess.DEVNULL,
                    shell=True), "Could not load data of tasks!")

    server = subprocess.Popen(f"{venv}/bin/python3 manage.py runserver",
                              stdout=subprocess.DEVNULL,
                              shell=True)

    print("[START] server started")
    gradle.wait() # wait until build process is finished, otherwise can not start program
        
    # run java backend    
    os.chdir(f"{cwd}/server")
    java = subprocess.Popen(f"java -jar ./libs/amazing-{version}.jar --port {java_port} {args} > /dev/null",
                            stdout=subprocess.DEVNULL,
                            shell=True
                           )
    print("[START] java started")

    # run client / frontend
    os.chdir(f"{cwd}/client")
    yarn = subprocess.Popen(f"yarn serve --host {host} --port {client_port}",
                            stdout=subprocess.DEVNULL,
                            shell=True
                           )
    print("[START] yarn serve started")

    # open website
    firefox = subprocess.Popen(f"firefox http://{host}:{client_port}",
                               stdout=subprocess.DEVNULL,
                               shell=True
                              )
    print("[START] firefox started")

    # script should keep running, otherwise background processes can not be killed with script
    while 1:
        time.sleep(1) # sleep because of RAM usage

if __name__ == "__main__":
    # catch signals to first kill subprocess and then exit
    signal.signal(signal.SIGINT, handler)
    signal.signal(signal.SIGTERM, handler)
    main()

