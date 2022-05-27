package soya.framework.commandline.tasks.markdown;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.commonmark.node.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import soya.framework.commandline.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Command(group = "markdown", name = "power-point-slide", httpMethod = Command.HttpMethod.POST)
public class MarkdownToXMLSlideShow extends MarkdownTask<XMLSlideShow> {


    @Override
    protected XMLSlideShow process() throws Exception {

        XMLSlideShow ppt = new XMLSlideShow();

        return ppt;
    }


    static class MarkdownVisitor extends AbstractVisitor {

        private List<Node> nodes = new ArrayList<>();

        @Override
        public void visit(BlockQuote blockQuote) {
            super.visit(blockQuote);
        }

        @Override
        public void visit(BulletList bulletList) {
            super.visit(bulletList);
        }

        @Override
        public void visit(Code code) {
            super.visit(code);
        }

        @Override
        public void visit(Document document) {
            super.visit(document);
        }

        @Override
        public void visit(Emphasis emphasis) {
            super.visit(emphasis);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            super.visit(fencedCodeBlock);
            nodes.add(fencedCodeBlock);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(Heading heading) {
            super.visit(heading);
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            super.visit(thematicBreak);
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            super.visit(htmlInline);
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            super.visit(htmlBlock);

            System.out.println(htmlBlock.getLiteral());
        }

        @Override
        public void visit(Image image) {
            super.visit(image);
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            super.visit(indentedCodeBlock);
        }

        @Override
        public void visit(Link link) {
            super.visit(link);
        }

        @Override
        public void visit(ListItem listItem) {
            super.visit(listItem);
        }

        @Override
        public void visit(OrderedList orderedList) {
            super.visit(orderedList);
        }

        @Override
        public void visit(Paragraph paragraph) {
            super.visit(paragraph);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            super.visit(softLineBreak);
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            super.visit(strongEmphasis);
        }

        @Override
        public void visit(Text text) {
            super.visit(text);
        }

        @Override
        public void visit(LinkReferenceDefinition linkReferenceDefinition) {
            super.visit(linkReferenceDefinition);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            super.visit(customBlock);
        }

        @Override
        public void visit(CustomNode customNode) {
            super.visit(customNode);
        }

        private static String getTitle(FencedCodeBlock block) {
            String token = block.getInfo();
            if (token.contains("[")) {
                token = token.substring(token.lastIndexOf("[") + 1, token.lastIndexOf("]"));
            }

            return token;
        }

        private static String getBody(FencedCodeBlock block) {
            List<String> items = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(block.getLiteral(), "\n");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if (token.startsWith("- ") || token.startsWith("-- ")) {
                    items.add(token.trim());
                }

            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(items.get(i));
            }

            return builder.toString();
        }

        private static List<String> getBodyAsList(FencedCodeBlock block) {
            List<String> items = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(block.getLiteral(), "\n");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                if (token.startsWith("- ") || token.startsWith("-- ")) {
                    items.add(token.trim());
                }

            }

            return items;
        }

        private static void setSlide(XSLFSlide slide, XSLFPictureData[] pictures) {
            CTBackgroundProperties backgroundProperties = slide.getXmlObject().getCSld().addNewBg().addNewBgPr();
            CTBlipFillProperties blipFillProperties = backgroundProperties.addNewBlipFill();
            CTRelativeRect ctRelativeRect = blipFillProperties.addNewStretch().addNewFillRect();
            String idx = slide.addRelation(null, XSLFRelation.IMAGES, pictures[0]).getRelationship().getId();
            CTBlip blib = blipFillProperties.addNewBlip();
            blib.setEmbed(idx);
        }
    }
}
