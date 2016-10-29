import Matlab.Nodes.UnitNode;
import Matlab.Recognizer.INotifier;
import Matlab.Recognizer.MRecognizer;
import Matlab.Transformer.NodeToAstTransformer;
import Matlab.Utils.IReport;
import Matlab.Utils.Message;
import Matlab.Utils.Result;
import abstractPattern.Action;
import ast.AspectDef;
import ast.CompilationUnits;

import java.util.HashMap;

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

    public static void main(String args[]) throws Exception {
        final String path = "/Users/k9/Desktop/AspectMATLAB/src/aspect.matlab";
        AspectDef aspectDef = (AspectDef) parseOrDie(path).getProgram(0);
        Action action = new Action(aspectDef.getAction(0).getAction(0), new HashMap<>(), x -> path);

        System.out.println(action.toString());
        System.out.println(action.getSourceCodePosition());
    }
}
