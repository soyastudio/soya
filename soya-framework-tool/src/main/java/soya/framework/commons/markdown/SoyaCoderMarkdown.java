package soya.framework.commons.markdown;

import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.commonmark.node.Image;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SoyaCoderMarkdown extends AbstractVisitor {
    public static final File home = new File("C:/github/SoyaCoder/website");

    //public static final Color FONT_COLOR = new Color(78, 147, 89);
    //public static final Color FONT_COLOR = new Color(33, 69, 18);
    // public static final Color FONT_COLOR = new Color(34, 82, 14);

    public static final Color FONT_COLOR = new Color(13, 52, 120);

    public static final File[] IMAGES = new File[]{
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/wave_green.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/blue.jpg")
            new File(home, "media/blue_elegant.jpg")
            //new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/blue_flourish.jpg")
            //new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/light_blue.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/white_flower.jpg")
            // new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/water_splash.jpg")
    };


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

    public static void main(String[] args) throws Exception {
        Parser parser = Parser.builder().build();
        Node document = parser.parseReader(new FileReader(new File(home, "markdown/spring_boot_overview.md")));

        SoyaCoderMarkdown visitor = new SoyaCoderMarkdown();
        document.accept(visitor);

        // Create presentation
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFPictureData[] pictures = new XSLFPictureData[]{
                ppt.addPicture(new FileInputStream(IMAGES[0]), PictureData.PictureType.JPEG)
        };

        XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);

        // Retriving the slide layout
        XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
        XSLFSlide cover = ppt.createSlide(layout);
        setSlide(cover, pictures);

        XSLFTextShape name = cover.getPlaceholder(0);
        // Clearing text to remove the predefined one in the template
        name.clearText();

        XSLFTextParagraph np = name.addNewTextParagraph();
        XSLFTextRun nt = np.addNewTextRun();
        nt.setText("Apache Avro");
        nt.setFontColor(FONT_COLOR);
        nt.setFontSize(48.);

        /*byte[] pictureData = IOUtils.toByteArray(new FileInputStream(new File("C:/github/SoyaCoder/SpringBoot/SpringFramework/media/avro_logo.png")));

        XSLFPictureData pd = ppt.addPicture(pictureData, PictureData.PictureType.PNG);
        XSLFPictureShape picture = cover.createPicture(pd);*/

        for (Node node : visitor.nodes) {
            if (node instanceof FencedCodeBlock) {
                FencedCodeBlock block = (FencedCodeBlock) node;
                // Creating the 1st slide
                XSLFSlide slide = ppt.createSlide(layout);
                setSlide(slide, pictures);

                XSLFTextShape title = slide.getPlaceholder(0);
                // Clearing text to remove the predefined one in the template
                title.clearText();

                XSLFTextParagraph p = title.addNewTextParagraph();
                XSLFTextRun r1 = p.addNewTextRun();
                r1.setText(getTitle(block));
                r1.setFontColor(FONT_COLOR);
                r1.setFontSize(42.);

                //selection of body placeholder
                XSLFTextShape body = slide.getPlaceholder(1);

                body.setLeftInset(100.);

                //clear the existing text in the slide
                body.clearText();

                List<String> items = getBodyAsList(block);
                for (String item : items) {
                    if (item.startsWith("- ")) {
                        //adding new paragraph
                        XSLFTextParagraph paragraph = body.addNewTextParagraph();
                        paragraph.setBulletAutoNumber(AutoNumberingScheme.arabicPeriod, 1);
                        XSLFTextRun textRun = paragraph.addNewTextRun();
                        textRun.setFontColor(FONT_COLOR);
                        textRun.setFontSize(24.);
                        textRun.setText(item.substring(2).trim());
                    } else if (item.startsWith("-- ")) {
                        XSLFTextParagraph paragraph = body.addNewTextParagraph();
                        paragraph.setIndentLevel(1);
                        XSLFTextRun textRun = paragraph.addNewTextRun();
                        textRun.setFontColor(FONT_COLOR);
                        textRun.setFontSize(20.);
                        textRun.setText(item.substring(3).trim());

                    }
                }

            }

        }

        // Save presentation
        FileOutputStream out = new FileOutputStream(new File(home, "SoyaCoder.pptx"));
        ppt.write(out);
        out.close();

        // Closing presentation
        ppt.close();
    }
}
