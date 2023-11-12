package markup;

import java.util.List;

public class Strikeout extends AbstractMarkClass {

    public Strikeout(List<Markdown> list) {
        super(list, "~", "<s>", "</s>");
    }
}
