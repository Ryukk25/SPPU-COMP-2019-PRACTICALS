import java.io.*;
import java.util.*;

public class PassOne {

    int lc = 0;
    int litptr = 0, poolptr = 0;
    int symIndex = 0;
    LinkedHashMap<String, TableRow> SYMTAB;
    ArrayList<TableRow> LITTAB;
    ArrayList<Integer> POOLTAB;

    public PassOne() {
        SYMTAB = new LinkedHashMap<>();
        LITTAB = new ArrayList<>();
        POOLTAB = new ArrayList<>();
        POOLTAB.add(0);
    }

    public static void main(String[] args) {
        PassOne one = new PassOne();
        try {
            one.parseFile();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void parseFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("input1.asm"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("IC.txt"));
        INSTtable lookup = new INSTtable();
        String line, code;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String parts[] = line.split("\\s+");
            if (parts.length == 0) continue;

            // ---------- START ----------
            if (parts[0].equalsIgnoreCase("START")) {
                lc = Integer.parseInt(parts[1]);
                code = "(AD,01)\t(C," + lc + ")";
                bw.write(code + "\n");
                continue;
            }

            // ---------- END ----------
            if (parts[0].equalsIgnoreCase("END")) {
                handleLTORG(bw);
                bw.write("(AD,02)\n");
                break;
            }

            // ---------- LTORG ----------
            if (parts[0].equalsIgnoreCase("LTORG")) {
                handleLTORG(bw);
                continue;
            }

            // ---------- ORIGIN ----------
            if (parts[0].equalsIgnoreCase("ORIGIN")) {
                lc = evaluateExpression(parts[1]);
                bw.write("(AD,03)\t(C," + lc + ")\n");
                continue;
            }

            // ---------- EQU ----------
            if (parts.length > 2 && parts[1].equalsIgnoreCase("EQU")) {
                int loc = evaluateExpression(parts[2]);
                SYMTAB.put(parts[0], new TableRow(parts[0], loc, ++symIndex));
                bw.write("(AD,04)\t(C," + loc + ")\n");
                continue;
            }

            // ---------- LABEL CHECK ----------
            String first = parts[0];
            String t = lookup.getType(first);
            boolean isMnemonicOrReg = (t != null && !t.isEmpty());

            if (!isMnemonicOrReg && !isDirective(first)) {
                if (SYMTAB.containsKey(first))
                    SYMTAB.put(first, new TableRow(first, lc, SYMTAB.get(first).getIndex()));
                else
                    SYMTAB.put(first, new TableRow(first, lc, ++symIndex));
            }

            // ---------- DS ----------
            if (parts.length > 1 && parts[1].equalsIgnoreCase("DS")) {
                int size = Integer.parseInt(parts[2].replace("'", ""));
                bw.write("(DL,02)\t(C," + size + ")\n");
                lc += size;
                continue;
            }

            // ---------- DC ----------
            if (parts.length > 1 && parts[1].equalsIgnoreCase("DC")) {
                int val = Integer.parseInt(parts[2].replace("'", ""));
                bw.write("(DL,01)\t(C," + val + ")\n");
                lc++;
                continue;
            }

            // ---------- Imperative Statements (mnemonic first) ----------
            if (lookup.getType(parts[0]).equals("IS")) {
                String mne = parts[0];
                code = "(IS,0" + lookup.getCode(mne) + ")\t";
                String code2 = "";

                for (int j = 1; j < parts.length; j++) {
                    parts[j] = parts[j].replace(",", "");
                    String operand = parts[j];

                    if (lookup.getType(operand).equals("RG")) {
                        code2 += lookup.getCode(operand) + "\t";
                    } else if (operand.contains("=")) {
                        operand = operand.replace("=", "").replace("'", "");
                        LITTAB.add(new TableRow(operand, -1, ++litptr));
                        code2 += "(L," + litptr + ")";
                    } else {
                        if (SYMTAB.containsKey(operand)) {
                            int ind = SYMTAB.get(operand).getIndex();
                            code2 += "(S," + ind + ")";
                        } else {
                            SYMTAB.put(operand, new TableRow(operand, -1, ++symIndex));
                            int ind = SYMTAB.get(operand).getIndex();
                            code2 += "(S," + ind + ")";
                        }
                    }
                }
                lc++;
                bw.write(code + code2 + "\n");
            }

            // ---------- Label then mnemonic (like L1 MOVER AREG, B) ----------
            else if (parts.length > 1 && lookup.getType(parts[1]).equals("IS")) {
                String label = parts[0];
                String mne = parts[1];
                code = "(IS,0" + lookup.getCode(mne) + ")\t";
                String code2 = "";

                for (int j = 2; j < parts.length; j++) {
                    parts[j] = parts[j].replace(",", "");
                    String operand = parts[j];

                    if (lookup.getType(operand).equals("RG")) {
                        code2 += lookup.getCode(operand) + "\t";
                    } else if (operand.contains("=")) {
                        operand = operand.replace("=", "").replace("'", "");
                        LITTAB.add(new TableRow(operand, -1, ++litptr));
                        code2 += "(L," + litptr + ")";
                    } else {
                        if (SYMTAB.containsKey(operand)) {
                            int ind = SYMTAB.get(operand).getIndex();
                            code2 += "(S," + ind + ")";
                        } else {
                            SYMTAB.put(operand, new TableRow(operand, -1, ++symIndex));
                            int ind = SYMTAB.get(operand).getIndex();
                            code2 += "(S," + ind + ")";
                        }
                    }
                }
                lc++;
                bw.write(code + code2 + "\n");
            }
        }

        bw.close();
        br.close();

        printSYMTAB();
        printLITTAB();
        printPOOLTAB();
    }

    // ---------- Helper Functions ----------

    private boolean isDirective(String s) {
        s = s.toUpperCase();
        return (s.equals("START") || s.equals("END") || s.equals("LTORG")
                || s.equals("ORIGIN") || s.equals("EQU") || s.equals("DS") || s.equals("DC"));
    }

    private void handleLTORG(BufferedWriter bw) throws IOException {
        int ptr = POOLTAB.get(poolptr);
        for (int j = ptr; j < LITTAB.size(); j++) {
            lc++;
            LITTAB.set(j, new TableRow(LITTAB.get(j).getSymbol(), lc));
            bw.write("(DL,01)\t(C," + LITTAB.get(j).symbol + ")\n");
        }
        poolptr++;
        POOLTAB.add(LITTAB.size());
    }

    private int evaluateExpression(String expr) {
        if (expr.contains("+")) {
            String[] p = expr.split("\\+");
            return SYMTAB.get(p[0]).getAddess() + Integer.parseInt(p[1]);
        } else if (expr.contains("-")) {
            String[] p = expr.split("\\-");
            return SYMTAB.get(p[0]).getAddess() - Integer.parseInt(p[1]);
        } else {
            return Integer.parseInt(expr);
        }
    }

    private void printSYMTAB() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("SYMTAB.txt"));
        System.out.println("\nSYMBOL TABLE");
        for (Map.Entry<String, TableRow> e : SYMTAB.entrySet()) {
            TableRow r = e.getValue();
            System.out.println(r.getIndex() + "\t" + r.getSymbol() + "\t" + r.getAddess());
            bw.write(r.getIndex() + "\t" + r.getSymbol() + "\t" + r.getAddess() + "\n");
        }
        bw.close();
    }

    private void printLITTAB() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("LITTAB.txt"));
        System.out.println("\nLITERAL TABLE");
        for (int i = 0; i < LITTAB.size(); i++) {
            TableRow r = LITTAB.get(i);
            System.out.println((i + 1) + "\t" + r.getSymbol() + "\t" + r.getAddess());
            bw.write((i + 1) + "\t" + r.getSymbol() + "\t" + r.getAddess() + "\n");
        }
        bw.close();
    }

    private void printPOOLTAB() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("POOLTAB.txt"));
        System.out.println("\nPOOLTAB");
        System.out.println("Index\t#first");
        for (int i = 0; i < POOLTAB.size(); i++) {
            System.out.println((i + 1) + "\t" + POOLTAB.get(i));
            bw.write((i + 1) + "\t" + POOLTAB.get(i) + "\n");
        }
        bw.close();
    }
}
