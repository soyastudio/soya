package soya.framework.albertsons.commands;

import com.google.common.base.CaseFormat;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ESQLBuilder {
    public static final String CONSTRUCTION = "CONSTRUCT";
    public static final String ASSIGNMENT = "assign";

    public static final String INPUT_ROOT = "_inputRootNode";
    public static final String JSONPATH_ROOT = "$.";
    public static final String VARIABLE_TOKEN = "$$.";

    public static final String PATTERN_CAST_TO_DECIMAL = "CAST({0} AS DECIMAL(10, 2))";

    public static void main(String[] args) {
        System.out.println(assignmentBuilder()
                .expression("$.orderTotal.snapEligibleTotal").pattern(PATTERN_CAST_TO_DECIMAL)
                .toString());

        System.out.println();

        ConstructionBuilder constructionBuilder = constructionBuilder();
        constructionBuilder.name("MiscChargesType").prefix("Abs:").parent("GroceryOrderHeader_");

        System.out.println(constructionBuilder.declare());
        System.out.println(constructionBuilder.create());

        System.out.println();

        LoopBuilder loopBuilder = loopBuilder().name("SubGroup").evaluation("$.payload.subGroup[*]");
        System.out.println(loopBuilder.declareVariable());
        System.out.println(loopBuilder.start());
        System.out.println(loopBuilder.move());
        System.out.println(loopBuilder.end());


    }

    public static AssignmentBuilder assignmentBuilder() {
        return new AssignmentBuilder();
    }

    public static ConstructionBuilder constructionBuilder() {
        return new ConstructionBuilder();
    }

    public static LoopBuilder loopBuilder() {
        return new LoopBuilder();
    }

    public static String valueOf(String exp) {
        if (exp.contains("(") && exp.contains(")") && exp.indexOf("(") < exp.indexOf(")")) {
            return exp.substring(exp.indexOf("(") + 1, exp.indexOf(")")).trim();
        }

        return exp;
    }

    public static class AssignmentBuilder {
        private String assignment;
        private Set<String> assignments = new LinkedHashSet<>();
        private String pattern;

        private AssignmentBuilder() {
        }

        public AssignmentBuilder expression(String expression) {

            String[] arr = expression.split("::");
            for (String function : arr) {
                String value = valueOf(function);
                if (assignment == null) {
                    assignment = value;
                }

                assignments.add(value);
            }

            return this;
        }

        public AssignmentBuilder assign(String assignment) {
            if (this.assignment == null) {
                this.assignment = assignment;
            }

            this.assignments.add(assignment);
            return this;
        }

        public AssignmentBuilder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();

            assignments.forEach(e -> {
                sb.append("assign(").append(e).append(")::");
            });
            String result = sb.toString();
            if(result.endsWith("::")) {
                result = result.substring(0, result.length() - 2);
            }

            return result;
        }

    }

    public static class ConstructionBuilder {
        private String name;
        private String prefix = "";
        private String parent;
        private String variable;

        private Map<String, LoopBuilder> loops = new LinkedHashMap<>();


        private ConstructionBuilder() {
        }

        public ConstructionBuilder name(String name) {
            this.name = name;
            if (variable == null) {
                variable = name + "_";
            }

            return this;
        }

        public ConstructionBuilder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public ConstructionBuilder parent(String parent) {
            this.parent = parent;
            return this;
        }

        public ConstructionBuilder variable(String variable) {
            this.variable = variable;
            return this;
        }

        public ConstructionBuilder addLoop(String exp) {
            String token = exp.trim();
            if (token.startsWith("ARRAY(") && token.endsWith(")")) {
                token = token.substring("ARRAY(".length(), token.length() - 1).trim();
            }

            if (!loops.containsKey(token)) {
                LoopBuilder loopBuilder = loopBuilder().name(name).evaluation(token);
                if (loops.size() > 0) {
                    loopBuilder.name(loopBuilder.name + loops.size());
                    loopBuilder.variable(loopBuilder.variable + loops.size());
                }

                loops.put(token, loopBuilder);
            }
            return this;
        }

        public String declare() {
            return new StringBuilder("DECLARE ")
                    .append(variable)
                    .append("  REFERENCE TO ")
                    .append(parent)
                    .append(";")
                    .toString();
        }

        public String create() {
            return new StringBuilder("CREATE LASTCHILD OF ")
                    .append(parent)
                    .append(" AS ")
                    .append(variable)
                    .append(" TYPE XMLNSC.Folder NAME '")
                    .append(prefix)
                    .append(name)
                    .append("';")
                    .toString();
        }
    }

    public static class LoopBuilder {

        private String name;
        private String variable;
        private String evaluation;

        private LoopBuilder() {
        }

        public LoopBuilder name(String name) {
            String token = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
            if (token.startsWith("_")) {
                token = token.substring(1);
            }

            if (token.endsWith("_LOOP")) {
                this.name = token;
                token = token.substring(0, token.length() - "_LOOP".length());
                token = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, token);
                if (!token.startsWith("_")) {
                    token = "_" + token;
                }

                this.variable = token;

            } else {
                this.name = token + "_LOOP";
                this.variable = "_" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, token);
            }

            return this;
        }

        public LoopBuilder variable(String variable) {
            this.variable = variable;
            return this;
        }

        public LoopBuilder evaluation(String evaluation) {
            this.evaluation = evaluation;
            return this;
        }

        public String declareVariable() {
            String token = evaluation;
            if (token.endsWith("[*]")) {
                token = token.substring(0, token.length() - 3) + ".Item";
            }

            if (token.startsWith(JSONPATH_ROOT)) {
                token = INPUT_ROOT + token.substring(1);
            }


            return new StringBuilder("DECLARE ")
                    .append(variable)
                    .append(" REFERENCE TO ")
                    .append(token)
                    .append(";").toString();
        }

        public String start() {
            return new StringBuilder(name)
                    .append(": WHILE LASTMOVE(").append(variable).append(") DO").toString();

        }

        public String move() {
            return new StringBuilder("MOVE ")
                    .append(variable)
                    .append(" NEXTSIBLING;")
                    .toString();
        }

        public String end() {
            return new StringBuilder("END WHILE ")
                    .append(variable)
                    .append(";")
                    .toString();
        }
    }


}
