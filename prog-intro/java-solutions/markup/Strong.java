package markup;

import java.util.List;

public class Strong extends AbstractMarkClass {
    
    public Strong(List<Markdown> list) {
        super(list, "__", "<strong>", "</strong>");
    }
}
