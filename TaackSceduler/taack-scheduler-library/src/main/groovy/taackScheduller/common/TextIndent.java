package taackScheduller.common;

import java.io.PrintWriter;

public class TextIndent {
    private static final String TAB = "    ";
    private final PrintWriter out;
    private final boolean perfo;
    private String tab;

    public TextIndent(String tab) {
        this.tab = tab;
        this.perfo = false;
        this.out = new PrintWriter(System.out, true);
    }

    public TextIndent(String tab, boolean perfo) {
        this.tab = tab;
        this.perfo = perfo;
        this.out = new PrintWriter(System.out, false);
    }

    public TextIndent(String tab, PrintWriter out) {
        this.tab = tab;
        this.perfo = false;
        this.out = out;
    }

    private void indent() {
        tab += TAB;
    }

    private void deIndent() {
        tab = tab.substring(TAB.length());
    }

    public void printIndent(String text) {
        if (!perfo) {
            printTabbed(text);
            indent();
        }
    }

    public void printDeIndent(String text) {
        if (!perfo) {
            deIndent();
            printTabbed(text);
        }
    }

    public void printTabbed(String text) {
        if (!perfo) out.println(tab + text);
    }

    public void flush() {
        out.flush();
    }
}
