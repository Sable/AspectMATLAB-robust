package aspectMATLAB;

import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import aspectMATLAB.serialization.decorators.ColumnNumberDecorator;
import aspectMATLAB.serialization.decorators.LineNumberDecorator;
import aspectMATLAB.serialization.decorators.OutputSupressedDecorator;
import aspectMATLAB.serialization.serializers.JSONSerializer;
import aspectMATLAB.serialization.serializers.XMLSerializer;
import ast.CompilationUnits;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

public class Main {
    public static CompilationUnits parseOrDie(String path) {
        Result<UnitNode> result = MRecognizer.RecognizeFile(path, true, new INotifier() {
            @Override
            public void Notify(String s, IReport iReport) {
                if (iReport.GetIsOk()) return;
                System.out.println(String.format("At file %s:", s));
                for (Message message : iReport) {
                    System.out.println(String.format("[%3d:%3d]\t%s:%s",
                            message.GetLine(),
                            message.GetColumn(),
                            message.GetSeverity(),
                            message.GetText()
                    ));
                    System.exit(1);
                }
            }
        });
        return NodeToAstTransformer.Transform(result.GetValue());
    }

    public static final void prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        System.out.println(out.toString());
    }

    public static void main(String args[]) throws Exception {
        final String path = "/Users/k9/Desktop/AspectMATLAB/src/aspectMATLAB/aspect.matlab";

        CompilationUnits compilationUnits = parseOrDie(path);

        /*
        InplaceProgramTransformer<StatementTracing, InplacePatternTransformer> transformer = new InplaceProgramTransformer<>(
                new StatementTracing(),
                new InplacePatternTransformer()
        );
        */

        compilationUnits.setIndentTab("    ");

        //transformer.transform(compilationUnits);
        System.out.println(compilationUnits.getPrettyPrinted());

        JSONSerializer serializerJSON = new JSONSerializer()
                .appendNumberDecorator(new ColumnNumberDecorator())
                .appendNumberDecorator(new LineNumberDecorator())
                .appendBooleanDecorator(new OutputSupressedDecorator());
        XMLSerializer serializerXML = new XMLSerializer()
                .appendNumberDecorator(new ColumnNumberDecorator())
                .appendNumberDecorator(new LineNumberDecorator())
                .appendBooleanDecorator(new OutputSupressedDecorator());

        System.out.println(serializerJSON.serializeAsString(compilationUnits));

        System.out.println(serializerXML.serializeAsString(compilationUnits));

    }
}
