import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class calculator_20231552 extends Frame {
    private TextField inputDisplay; // 입력 창
    private boolean newInput = true; // 새로운 입력 여부
    // 음수 입력 플래그
    private String operator = ""; // 현재 연산자
    // 파스텔 톤 색상 정의
    Color pastelPurple = new Color(244, 227, 252); // 파스텔 배경
    Color pastelButton = new Color(248, 240, 250); // 파스텔 버튼

    public calculator_20231552() {
        // 기본 레이아웃 설정
        setLayout(null);
        // 입력 창 초기화
        inputDisplay = new TextField("0");
        inputDisplay.setEditable(false);
        inputDisplay.setFont(new Font("Arial", Font.BOLD, 36));
        inputDisplay.setBackground(pastelPurple);
        inputDisplay.setBounds(20, 20, 360, 80); // 위치 및 크기 설정
        add(inputDisplay);

        // 버튼 패널 초기화
        Panel buttons = new Panel();

        buttons.setLayout(new GridLayout(5, 6)); // 버튼 배열
        buttons.setBounds(20, 120, 360, 400);


        // 버튼 라벨 정의
        String[] labels = {
                " ", "x!", "(", ")", "%", "AC",
                "sin", "ln", "7", "8", "9", "+",
                "cos", "log", "4", "5", "6", "*",
                "tan", "√", "1", "2", "3", "-",
                " ", "x^y", "0", ".", "=", "/"
        };

        for (String label : labels) {
            Button button = new Button(label);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(new ButtonHandler());
            button.setBackground(pastelButton);
            buttons.add(button);
        }

        add(buttons);

        // 프레임 설정
        setTitle("calculator_20231552");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // 창 닫기 이벤트 처리
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = e.getActionCommand();

            switch (input) {
                case "AC":
                    resetCalculator();
                    break;
                case ".":
                    handleDecimalPoint();
                    break;
                case "=":
                    performCalculation();
                    break;
                case "x!":
                    handleFactorial();
                    break;
                case "x^y":
                    handlePower();
                    break;
                case "(":
                case ")":
                    handleParenthesis(input);
                    break;
                default:
                    if (input.matches("[0-9]")) {
                        handleNumberInput(input);
                    } else if (input.matches("[+\\-*/%]")) {
                        handleOperator(input);
                    } else if (input.matches("sin|cos|tan|log|ln|√")) {
                        handleFunction(input);
                    }
                    break;
            }
        }

        ////////////////////////출력 기능/////////////////////////////
        private void handleFactorial() {
            inputDisplay.setText(inputDisplay.getText() + "!");
            newInput = false;
        }

        private void handlePower() {
            inputDisplay.setText(inputDisplay.getText() + "^");
            newInput = false;
        }

        private void handleFunction(String input) {
            if (newInput) {
                inputDisplay.setText(input);
            } else {
                inputDisplay.setText(inputDisplay.getText() + input);
            }
            newInput = false;
        }

        // 소수점 처리
        private void handleDecimalPoint() {
            String currentText = inputDisplay.getText();
            // 새로운 입력 상태인지 확인
            if (newInput) {
                inputDisplay.setText("0.");
            }//이전 입력이 연산자로 끝났는지 확인
            else if (currentText.endsWith("+") || currentText.endsWith("-") ||
                    currentText.endsWith("*") || currentText.endsWith("/") ||
                    currentText.endsWith("%") || currentText.endsWith("^") || currentText.endsWith("(")) {

                inputDisplay.setText(currentText + "0.");
            }//입력된 텍스트에서 마지막 숫자에 소수점이 없는 경우
            else if (!currentText.matches(".*\\d\\.\\d*$")) {
                inputDisplay.setText(currentText + ".");
            }
            newInput = false;
        }


        // 숫자 입력 처리
        private void handleNumberInput(String input) {
            if (newInput || inputDisplay.getText().equals("0")) {
                inputDisplay.setText(input);
            } else {
                inputDisplay.setText(inputDisplay.getText() + input);
            }
            newInput = false;
        }

        private void handleOperator(String input) {
            // 음수 처리: 연산자 직후 "-" 입력 시 음수로 해석
            if (input.equals("-") && (newInput || inputDisplay.getText().endsWith(operator))) {
                if (newInput) {
                    inputDisplay.setText("-");
                } else {
                    inputDisplay.setText(inputDisplay.getText() + input);
                }
                newInput = false;
                return;
            }

            // 연속된 연산자 입력 시 마지막 연산자로 대체
            if (!operator.isEmpty() && !input.equals("-") && inputDisplay.getText().endsWith(operator)) {
                // 마지막 연산자를 새로운 연산자로 교체
                String currentText = inputDisplay.getText();
                inputDisplay.setText(currentText.substring(0, currentText.length() - 1) + input);
                operator = input;
                newInput = false;
                return;
            }
            // 새 연산자 입력 처리'
            operator = input; // 현재 연산자 저장
            inputDisplay.setText(inputDisplay.getText() + operator); // 입력 창에 연산자 추가
            newInput = false; // 다음 입력을 위한 상태 초기화

        }

        private void handleParenthesis(String input) {
            String currentText = inputDisplay.getText();
            // 입력 창에 괄호 추가
            if (newInput || currentText.equals("0")) {
                inputDisplay.setText(input); // 새로운 입력으로 괄호를 시작
            } else {
                inputDisplay.setText(currentText + input); // 기존 입력에 괄호 추가
            }
            newInput = false;
        }

        ///////////////////////////////계산 기능 수행///////////////////////////////
        // 연산 수행
        private void performCalculation() {
            try {
                String input = inputDisplay.getText();
                Stack<Double> values = new Stack<>(); //숫자들의 집합
                Stack<Character> operators = new Stack<>(); //연산자들의 집합
                StringBuilder currentNumber = new StringBuilder(); // 현재 숫자를 저장할 변수

                for (int i = 0; i < input.length(); i++) {
                    char ch = input.charAt(i);

                    if (Character.isDigit(ch)) {
                        currentNumber.append(ch);
                    } else if (ch == '.') {
                        // 현재 숫자에 소수점이 없는 경우만 추가
                        if (!currentNumber.toString().contains(".")) {
                            currentNumber.append(".");
                        }
                    } else {
                        // 숫자를 스택에 추가
                        if (!currentNumber.isEmpty()) {
                            values.push(Double.parseDouble(currentNumber.toString()));
                            currentNumber.setLength(0); // 숫자 초기화
                        }

                        if (ch == '-') {
                            // '-' 처리
                            if (currentNumber.isEmpty() && (values.isEmpty() || !Character.isDigit(input.charAt(i - 1)))) {
                                // 음수로 해석
                                currentNumber.append(ch);
                            } else {
                                // 연산자로 처리
                                if (!currentNumber.isEmpty()) {
                                    values.push(Double.parseDouble(currentNumber.toString()));
                                    currentNumber.setLength(0); // 숫자 초기화
                                }
                                // 연산자 우선순위 처리
                                while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(ch)) {
                                    executeOperation(values, operators.pop());
                                }
                                operators.push(ch);
                            }
                        } else if ("+*/".indexOf(ch) >= 0) {
                            // 연산자 처리
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0); // 숫자 초기화
                            }
                            // 연산자 우선순위 처리
                            while (!operators.isEmpty() && getPriority(operators.peek()) >= getPriority(ch)) {
                                executeOperation(values, operators.pop());
                            }
                            operators.push(ch);
                        }  if (ch == '!') {
                            // 팩토리얼 연산 처리
                            double value = values.pop();
                            double result = factorial((int) value);
                            values.push(result); // 결과 스택에 추가
                            inputDisplay.setText(String.valueOf(result)); // 결과 표시
                            return;
                        } else if (ch == '%') {
                            // 퍼센트 연산 처리
                            double value = values.pop();
                            double result = percentage(value);
                            values.push(result); // 결과 스택에 추가
                            inputDisplay.setText(String.valueOf(result)); // 결과 표시
                            return;
                        } else if (input.startsWith("log", i)) {
                            // log 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('L'); // 'L'은 log를 나타냄
                            i += 2; // log는 3문자
                        } else if (input.startsWith("ln", i)) {
                            // ln 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('N'); // 'N'은 ln을 나타냄
                            i += 1; // ln은 2문자
                        } else if (ch == '√') {
                            // √ 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('R'); // 'R' root를 나타냄
                        } else if (ch == '^') {
                            // ^ 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('P'); // 'P' 를 power 나타냄
                        } else if (input.startsWith("cos", i)) {
                            // cos 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('C'); // 'C'은 cos를 나타냄
                            i += 2;
                        } else if (input.startsWith("sin", i)) {
                            // sin 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('S'); // 'S'은 sin을 나타냄
                            i += 2;
                        } else if (input.startsWith("tan", i)) {
                            // tan 연산자 추가
                            if (!currentNumber.isEmpty()) {
                                values.push(Double.parseDouble(currentNumber.toString()));
                                currentNumber.setLength(0);
                            }
                            operators.push('T'); // 'T'은 tan를 나타냄
                            i += 2;
                        } else if (ch == '(') {
                            operators.push(ch);
                        } else if (ch == ')') {
                            // 닫는 괄호 처리
                            while (!operators.isEmpty() && operators.peek() != '(') {
                                executeOperation(values, operators.pop());
                            }
                            if (!operators.isEmpty() && operators.peek() == '(') {
                                operators.pop(); // 여는 괄호 제거
                            } else {
                                inputDisplay.setText("Error: Mismatched parentheses");
                                return;
                            }
                        }
                    }


                }
                // 마지막 남은 숫자를 스택에 추가
                if (!currentNumber.isEmpty()) {
                    values.push(Double.parseDouble(currentNumber.toString()));
                }
                // 남은 연산자 처리
                while (!operators.isEmpty()) {
                    executeOperation(values, operators.pop());
                }
                // 결과 출력
                if (!values.isEmpty()) {
                    inputDisplay.setText(String.valueOf(values.pop()));
                    newInput = true;
                }
            } catch (Exception e) {
                inputDisplay.setText("Error");
            }
        }

        // 연산 우선순위
        private int getPriority(char op) {
            return switch (op) {
                case '(', ')' -> -1; // 괄호는 별도 처리
                case '+', '-' -> 1;
                case '*', '/' -> 2;
                default -> 0;
            };
        }

        // 스택에서 연산 실행
        private void executeOperation(Stack<Double> values, Character operator) {
            if (values.isEmpty()) return;

            if ("SCTRNL".indexOf(operator) >= 0) {
                double a = values.pop();
                values.push(applyMathFunction(operator, a));
            } else {
                if (values.size() < 2) return;
                double b = values.pop();
                double a = values.pop();

                switch (operator) {
                    case '+':
                        values.push(a + b);
                        break;
                    case '-':
                        values.push(a - b);
                        break;
                    case '*':
                        values.push(a * b);
                        break;
                    case '/':
                        if (b == 0) {
                            inputDisplay.setText("error");
                            values.clear();
                            return;
                        }
                        values.push(a / b);
                        break;
                    case 'P':
                        values.push(Math.pow(a, b));
                }
            }
        }

        // 계산기 초기화
        private void resetCalculator() {
            inputDisplay.setText("0");
            newInput = true;
            operator = "";
        }
        private double factorial(int n) {
            double result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        }
        // 퍼센트 계산
        private double percentage(double value) {
            return value / 100;
        }
        private double applyMathFunction(char operator, double value) {

            switch (operator) {
                case 'S':
                    return Math.sin(Math.toRadians(value));
                case 'C':
                    return Math.cos(Math.toRadians(value));
                case 'T':
                    return Math.tan(Math.toRadians(value));
                case 'L':
                    return Math.log10(value);
                case 'N':
                    return Math.log(value);
                case 'R':
                    return Math.sqrt(value);
                default:
                    return 0;
            }
        }
    }

    public static void main(String[] args) {
        new calculator_20231552();
    }
}