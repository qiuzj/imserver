package cn.javaee.im.command;

public abstract class BaseCommand {
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
