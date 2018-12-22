package Tasks;

class Process {


    protected String name;
    protected String owner;
    protected byte priority;
    private int cycles = 0;
    protected int launches;

    protected Process(String name, String owner, byte priority, int launches) {

        this.name = name;
        this.owner = owner;
        this.priority = priority;
        this.launches = launches;
    }

    protected int priority() {

        if (cycles < Integer.MAX_VALUE - priority) {
            cycles++;
        }
        return priority + cycles;
    }

    protected void start() {

        cycles = 0;
        if (launches > 0) launches--;
    }

    protected void pause() {}

    protected boolean isEnd() {
        return launches == 0;
    }

    public String getName() {
        return name;
    }

    public byte getPriority() {
        return priority;
    }

    public int getSystemPriority() {
        return priority + cycles;
    }

    public int getLaunches() {
        return launches;
    }
}