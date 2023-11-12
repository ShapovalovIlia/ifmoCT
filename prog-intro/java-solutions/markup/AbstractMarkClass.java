package markup;

import java.util.List;

public abstract class AbstractMarkClass implements Markdown{
    private final List<Markdown> markupList;
    private final String startHtml;
    private final String endHtml;
    private final String mdSymbol;

    public AbstractMarkClass(List<Markdown> list, String md, String sHtml, String eHtml) {
        markupList = list;
        mdSymbol = md;
        endHtml = eHtml;
        startHtml = sHtml;
    }

    public void toMarkdown(StringBuilder strb) {
        strb.append(mdSymbol);
        for (Markdown temp: markupList) {
            temp.toMarkdown(strb);
        } 
        strb.append(mdSymbol);
    }

    public void toHtml(StringBuilder strb) {
        strb.append(startHtml);
        for (Markdown temp: markupList) {
            temp.toHtml(strb);
        } 
        strb.append(endHtml);
    }
}
