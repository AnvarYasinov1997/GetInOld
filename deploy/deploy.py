#!/usr/bin/python

import os
import re
import subprocess
import sys
import time
import threading

serverIp = "35.238.70.65"


def check_backup_application_state():
    status = subprocess.check_output(
        "curl http://" + serverIp + ":80/backup", shell=True)
    statusStr = str(status)
    return statusStr == "b'200'"


def run_tests():
    status = os.system(
        "../gradlew -p ../ test")
    if status == 0:
        print("Test success completed")
    else:
        raise Exception("Test failure")


def compile_source_code_into_a_jar_file():
    status = os.system(
        "../gradlew -p ../ bootJar")
    if status == 0:
        print("Jar file compiled")
    else:
        raise Exception("Jar file compile error")


def copy_old_jar_file_to_backup_dir():
    status = os.system(
        "ssh " + serverIp + " 'cp /home/anvar/test-1.0-SNAPSHOT.jar /home/anvar/backup'")
    if status == 0:
        print("Jar file copy to backup dir success")
    else:
        raise Exception("Jar file copy to backup error")


def load_jar_file_to_server():
    status = os.system(
        "scp ../test/build/libs/test-1.0-SNAPSHOT.jar " + serverIp + ":/home/anvar/")
    if status == 0:
        print("Jar file uploaded to server")
    else:
        raise Exception("Jar file upload error")


def revert_old_jar_file_from_backup_dir():
    status = os.system("ssh " + serverIp +
                       " 'cp /home/anvar/backup/test-1.0-SNAPSHOT.jar /home/anvar/'")


def kill_the_process_by_port(port):
    status = os.system("ssh " + serverIp +
                       " 'sudo fuser -k " + str(port) + "/tcp'")
    if status == 0:
        print("Kill the old application insatnce by port " + str(port))
    else:
        raise Exception("Missing process on port" + str(port))


def run_java_application():
    status = os.system("ssh " + serverIp + " 'sudo /home/anvar/dep.sh'")
    if status == 0:
        print("Run new application instance success")
    else:
        raise Exception("Jar file run error")


def check_main_application_state(port):
    url = "http://" + serverIp + str(port)
    tryCount = 0
    while tryCount < 10:
        status = subprocess.check_output(
            "curl http://35.238.70.65:80/main", shell=True)
        statusStr = str(status)
        print(statusStr)
        if statusStr == "b'200'":
            return 0
        else:
            time.sleep(1)
            tryCount = tryCount + 1
    return 1


def check_output_log_file():
    return os.system("ssh " + serverIp + " 'cat /home/anvar/stdio.txt'")


def main(args):
    if check_backup_application_state():
        try:
            run_tests()
            compile_source_code_into_a_jar_file()
            copy_old_jar_file_to_backup_dir()
            load_jar_file_to_server()
            kill_the_process_by_port(8083)
            run_java_application()
            if check_main_application_state(8083) == 0:
                print("Deployment success")
            else:
                print("Deployment error")
                check_output_log_file()
                revert_old_jar_file_from_backup_dir()
                run_java_application()
                if check_backup_application_state(8083) == 1:
                    print("Backup application deployment error")
                    check_output_log_file()
                    raise Exception("Backup application error runing")
        except Exception as e:
            print(str(e))
    else:
        print("Backup applicationdoes not runing")


if __name__ == "__main__":
    main([])
