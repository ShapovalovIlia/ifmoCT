package markup;

import java.util.List;

public class Emphasis extends AbstractMarkClass{

    public Emphasis(List<Markdown> list) {
        super(list, "*", "<em>", "</em>");
    }
}
