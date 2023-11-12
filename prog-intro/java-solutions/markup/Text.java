package markup;

public class Text implements Markdown{
private String textStr = "";
    
    public Text(String str){
        textStr = str;
    }
    
    @Override
    public void toMarkdown(StringBuilder strb) {
        strb.append(textStr);
    }

    @Override
    public void toHtml(StringBuilder strb) {
        strb.append(textStr);
    }
}
