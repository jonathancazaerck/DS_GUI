package ds3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Gui {
    private JList fileList;
    private JButton herladenButton;
    private JButton downloadButton;
    private JButton openButton;
    private JButton deleteButton;
    private JPanel jpanel;
    private JLabel LabelSelectedItem;
    private JButton nodeInfoButton;
    private ArrayList<String> input;
    private String selectedItem;
    private String nodeName;
    private String strNodeIp;
    private InetAddress nodeIp;
    private int nodePort;
    private String nodeFolder;

    public Gui() {
        input = new ArrayList<String>();
        opvullen();
        herladenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        });

        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = fileList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Object o = fileList.getModel().getElementAt(index);
                        selectedItem = o.toString();
                        LabelSelectedItem.setText(selectedItem);
                    }
                }
            }
        });

        downloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedItem != null) {
                    JOptionPane.showMessageDialog(jpanel, "Het bestand: " + selectedItem + " wordt gedownloaded.", "Bericht", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showError();
                }
            }
        });

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedItem != null) {
                    JOptionPane.showMessageDialog(jpanel, "Het bestand: " + selectedItem + " wordt geopend.", "Bericht", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showError();
                }
            }
        });

        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedItem != null) {
                    String[] options = {"Ja", "Nee"};
                    int i = JOptionPane.showOptionDialog(jpanel, "Wilt u het bestand: " + selectedItem + " verwijderen?", "Bericht", 0, JOptionPane.WARNING_MESSAGE, null, options, null);
                    if (i == 0)
                        System.out.println("File will be deleted!");
                    else
                        System.out.println("User makes a mistake!");
                } else {
                    showError();
                }
            }
        });

        nodeInfoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNodeInfo();
            }
        });
        setNodeInfo();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("SystemY");
        frame.setContentPane(new Gui().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void reload() {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < input.size(); i++) {
            listModel.addElement(input.get(i));
        }
        fileList.setModel(listModel);
    }

    public void opvullen() {
        input.add("test1.txt");
        input.add("test2.txt");
        input.add("test3.txt");
    }

    public void showError() {
        JOptionPane.showMessageDialog(jpanel, "Er is geen bestand geselecteerd. Het proces wordt afgebroken.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showNodeInfo() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Naam:", SwingConstants.RIGHT));
        label.add(new JLabel("IP-adres:", SwingConstants.RIGHT));
        label.add(new JLabel("Poort:", SwingConstants.RIGHT));
        label.add(new JLabel("Map:", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        controls.add(new JLabel(nodeName));
        controls.add(new JLabel(nodeIp.toString()));
        controls.add(new JLabel(Integer.toString(nodePort)));
        controls.add(new JLabel(nodeFolder));
        panel.add(controls, BorderLayout.CENTER);
        String[] options = {"OK", "Aanpassen"};
        if (JOptionPane.showOptionDialog(null, panel, "Info", 0, JOptionPane.QUESTION_MESSAGE, null, options, null) == 1)
            setNodeInfo();
    }

    public int setNodeInfoWindow() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Naam:", SwingConstants.RIGHT));
        label.add(new JLabel("IP-adres:", SwingConstants.RIGHT));
        label.add(new JLabel("Poort:", SwingConstants.RIGHT));
        label.add(new JLabel("Map:", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField FNodeName = new JTextField();
        controls.add(FNodeName);
        JTextField FNodeIP = new JTextField();
        controls.add(FNodeIP);
        JTextField FNodePort = new JTextField();
        controls.add(FNodePort);
        JTextField FNodeFolder = new JTextField();
        controls.add(FNodeFolder);

//        JFileChooser f = new JFileChooser();
//        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        f.showSaveDialog(null);
//        //nodeFolder = f.getCurrentDirectory() + f.getSelectedFile()
//        nodeFolder = new StringBuilder().append(f.getCurrentDirectory()).append("/").append(f.getSelectedFile()).toString();
//        controls.add(f);

        panel.add(controls, BorderLayout.CENTER);

        int i = JOptionPane.showConfirmDialog(jpanel, panel, "Info", JOptionPane.OK_CANCEL_OPTION);
        nodeName = FNodeName.getText();
        strNodeIp = FNodeIP.getText();
        nodePort = Integer.parseInt(FNodePort.getText());
        nodeFolder = FNodeFolder.getText();
        return i;
    }

    public void setNodeInfo() {
        int i = setNodeInfoWindow();
        if (i == JOptionPane.CANCEL_OPTION) {
            setNodeInfo();
        } else if (!validateIP(strNodeIp)) {
            setNodeInfo();
        } else {
            try {
                nodeIp = InetAddress.getByName(strNodeIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean validateIP(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jpanel = new JPanel();
        jpanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 3, new Insets(20, 20, 20, 20), -1, -1));
        jpanel.setPreferredSize(new Dimension(800, 600));
        fileList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        fileList.setModel(defaultListModel1);
        jpanel.add(fileList, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 6, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(283, 50), null, 0, false));
        herladenButton = new JButton();
        herladenButton.setEnabled(true);
        herladenButton.setText("Herladen");
        jpanel.add(herladenButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, -1), null, null, 0, false));
        downloadButton = new JButton();
        downloadButton.setText("Download");
        jpanel.add(downloadButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, -1), null, null, 0, false));
        openButton = new JButton();
        openButton.setText("Open");
        jpanel.add(openButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, -1), null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Cazaerck Jonathan - Claes Jill - Havermans Elias - Wirtz Hans Otto");
        jpanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Verwijderen");
        jpanel.add(deleteButton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, -1), null, null, 0, false));
        LabelSelectedItem = new JLabel();
        LabelSelectedItem.setForeground(new Color(-16776961));
        LabelSelectedItem.setText("");
        jpanel.add(LabelSelectedItem, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Geselecteerd:");
        label2.setVerticalAlignment(0);
        label2.setVerticalTextPosition(1);
        jpanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(87, 16), null, 0, false));
        nodeInfoButton = new JButton();
        nodeInfoButton.setText("Node info");
        jpanel.add(nodeInfoButton, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(110, -1), null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanel;
    }
}




