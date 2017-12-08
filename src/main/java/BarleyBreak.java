/**
 * Created by entony on 06.12.2017.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class BarleyBreak extends JFrame {
    private Color buttonColor = Color.WHITE;
    private JPanel panel = new JPanel(new GridLayout(4, 4, 2, 2));
    private int[][] numbers = new int[4][4];

    public BarleyBreak() {
        super("Игра \"15\"");

        setBounds(200, 200, 300, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenu();

        Container container = getContentPane();
        panel.setDoubleBuffered(true);
        container.add(panel);

        generate();
        repaintField();
    }

    private class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if ("выход".equals(command)) {
                System.exit(0);
            }
            if ("новая игра".equals(command)) {
                generate();
                repaintField();
            }
            if ("решить".equals(command)) {
                solve();
                repaintField();
            }
            if ("выбрать цвет".equals(command)){
                buttonColor = JColorChooser.showDialog(null, "Выберите цвет", Color.WHITE);
                repaintField();
            }
        }
    }

    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setVisible(false);
            String name = button.getText();
            change(Integer.parseInt(name));
        }
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("Основное");
        JMenu viewMenu = new JMenu("Вид");

        for (String fileItem : new String [] { "Новая игра", "Решить", "Выход" }) {
            JMenuItem item = new JMenuItem(fileItem);
            item.setActionCommand(fileItem.toLowerCase());
            item.addActionListener(new NewMenuListener());
            fileMenu.add(item);
        }
        fileMenu.insertSeparator(1);
        fileMenu.insertSeparator(3);

        String viewItem = "Выбрать цвет";
        JMenuItem item = new JMenuItem(viewItem);
        item.setActionCommand(viewItem.toLowerCase());
        item.addActionListener(new NewMenuListener());
        viewMenu.add(item);

        menu.add(fileMenu);
        menu.add(viewMenu);
        setJMenuBar(menu);
    }

    public void repaintField() {
        panel.removeAll();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                JButton button = new JButton(Integer.toString(numbers[i][j]));
                button.setBackground(buttonColor);
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setFont(new Font("Helvetica", Font.PLAIN, 20));
                button.setFocusable(false);
                panel.add(button);
                if (numbers[i][j] == 0) {
                    button.setVisible(false);
                } else {
                    button.addActionListener(new ClickListener());
                }
            }
        }

        panel.validate();
        panel.repaint();
    }

    public void generate() {
        Random generator = new Random();
        int[] invariants = new int[16];

        do {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    numbers[i][j] = 0;
                    invariants[i * 4 + j] = 0;
                }
            }

            for (int i = 1; i < 16; i++) {
                int k, l;
                do {
                    k = generator.nextInt(4);
                    l = generator.nextInt(4);
                }
                while (numbers[k][l] != 0);
                numbers[k][l] = i;
                invariants[k * 4 + l] = i;
            }
        }
        while (!canBeSolved(invariants));
    }

    private boolean canBeSolved(int[] invariants) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (invariants[i] == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (invariants[j] < invariants[i])
                    sum ++;
            }
        }
        return sum % 2 == 0;
    }

    private void solve(){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                if (i*4+j+1 == 15) {
                    numbers[i][j] = 0;
                    numbers[i][j+1] = 15;
                    break;
                }
                numbers[i][j] = i*4 + j + 1;
            }
        }

    }

    public boolean checkWin() {
        boolean status = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 && j > 2)
                    break;
                if (numbers[i][j] != i * 4 + j + 1) {
                    status = false;
                }
            }
        }
        return status;
    }


    public void change(int num) {
        int i = 0, j = 0;
        for (int k = 0; k < 4; k++) {
            for (int l = 0; l < 4; l++) {
                if (numbers[k][l] == num) {
                    i = k;
                    j = l;
                }
            }
        }
        if (i > 0) {
            if (numbers[i - 1][j] == 0) {
                numbers[i - 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (i < 3) {
            if (numbers[i + 1][j] == 0) {
                numbers[i + 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (j > 0) {
            if (numbers[i][j - 1] == 0) {
                numbers[i][j - 1] = num;
                numbers[i][j] = 0;
            }
        }
        if (j < 3) {
            if (numbers[i][j + 1] == 0) {
                numbers[i][j + 1] = num;
                numbers[i][j] = 0;
            }
        }

        repaintField();

        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "ВЫ ВЫИГРАЛИ!", "ПОЗДРАВЛЯЕМ!", JOptionPane.INFORMATION_MESSAGE);
            generate();
            repaintField();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{new BarleyBreak().setVisible(true); });
    }
}
