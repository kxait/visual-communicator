package pl.edu.pk.kron.visualcommunicator.model.message_contents;

public class Error {
    private final boolean fatal;
    private final String content;

    public Error(boolean fatal, String content) {
        this.fatal = fatal;
        this.content = content;
    }

    public boolean isFatal() {
        return fatal;
    }

    public String getContent() {
        return content;
    }
}
