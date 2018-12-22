package Tasks;

import Shell.Variables;
import UI.Home.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Manager implements Runnable {

    protected Thread thread;
    protected boolean running;
    protected Controller controller;
    protected ArrayList<Process> processes = new ArrayList<>();
    protected Process currentProcess = null;

    public Manager(Controller controller) {
        this.controller = controller;
        thread = new Thread(this);

        thread.start();
        running = true;
    }

    public void close() {
        running = false;

        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            startNext();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void startNext() {

        if (currentProcess != null && currentProcess.isEnd()) {
            processes.remove(currentProcess);
        }
        currentProcess = getNextProcess();
        ArrayList<String> captions = processes.stream().map(process -> process.name + " \t" + process.owner + "P" + process.priority + "L" + process.launches + "SP" + process.getSystemPriority()).collect(Collectors.toCollection(ArrayList::new));

        if (currentProcess != null) {
            currentProcess.start();
            controller.showProcess(currentProcess.name, captions);
        } else {
            controller.showProcess("", captions);
        }
    }

    protected Process getNextProcess() {

        Process nextProcess = null;
        int nextProcessPriority = -1;
        for (Process current : processes) {
            int currentPriority = current.priority();
            if (nextProcessPriority < currentPriority) {
                nextProcess = current;
                nextProcessPriority = currentPriority;
            }
        }
        return nextProcess;
    }

    public boolean newProcess(String name, byte priority, int launches) {

        for (Process process : processes) {
            if (name.equals(process.name)) {
                return false;
            }
        }
        processes.add(new Process(name, Variables.user.getUsername(), priority, launches));
        return true;
    }

    public boolean killProcess(String name) {

        for (Process process : processes) {
            if (name.equals(process.name)) {
                if (Variables.user.isAdmin() || process.owner.equals(Variables.user.getUsername())) {
                    processes.remove(process);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public boolean killProcessRights(String name) {
        for (Process process : processes) {
            if (name.equals(process.name)) {
                return Variables.user.isAdmin() || process.owner.equals(Variables.user.getUsername());
            }
        }
        return true;
    }

    public boolean newPriority(String name, byte priority) {

        for (Process process : processes) {
            if (name.equals(process.name)) {
                process.priority = priority;
                return true;
            }
        }
        return false;
    }
}