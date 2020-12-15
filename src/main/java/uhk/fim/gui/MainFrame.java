package uhk.fim.gui;

import com.google.gson.Gson;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uhk.fim.database.CsvDataManager;
import uhk.fim.database.DataManager;
import uhk.fim.database.JdbcDataManager;
import uhk.fim.model.ShoppingCart;
import uhk.fim.model.ShoppingCartItem;

import javax.swing.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;

public class MainFrame extends JFrame implements ActionListener {
    JPanel panelMain;

    // Tlačítka deklarujeme zde, abychom k nim měli přístup v metodě actionPerformed
    JButton btnInputAdd;
    JTextField txtInputName, txtInputPricePerPiece;
    JSpinner spInputPieces;

    // Labels
    JLabel lblTotalPrice;

    ShoppingCart shoppingCart;
    ShoppingCartTableModel shoppingCartTableModel;

    DataManager dataManager;
    final String STORAGE_FILE_CSV = "src/main/resources/storage.csv";

    public MainFrame(int width, int height) {
        super("PRO2 - Shopping cart");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Vytvoříme košík (data)
        shoppingCart = new ShoppingCart();

        // Načteme data ze zdroje
        try {
            dataManager = new JdbcDataManager("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:SampleDB");
            //dataManager = new CsvDataManager(STORAGE_FILE_CSV);
            shoppingCart.setItems(dataManager.getAllShoppingCartItems());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Vytvoříme model
        shoppingCartTableModel = new ShoppingCartTableModel();
        // Propojíme model s košíkem (data)
        shoppingCartTableModel.setShoppingCart(shoppingCart);

        initGUI();

        updateFooter();
    }

    public void initGUI() {
        // Vytvoříme hlavní panel, do kterého budeme přidávat další (pod)panely.
        // Naším cílem při tvorbě GUI, je snaha jednotlivé komponenty zanořovat.
        panelMain = new JPanel(new BorderLayout());

        // Menu
        createMenuBar();

        // Vytvoříme další 3 panely. Panel pro prvky formuláře pro přidání položky.
        // Panel pro tabulku a panel pro patičku.
        JPanel panelInputs = new JPanel(new FlowLayout(FlowLayout.LEFT)); // FlowLayout LEFT - komponenty chceme zarovnat zleva doprava.
        JPanel panelTable = new JPanel(new BorderLayout());
        JPanel panelFooter = new JPanel(new BorderLayout());

        // *** Formulář pro přidání položky ***
        // Název
        JLabel lblInputName = new JLabel("Název: ");
        txtInputName = new JTextField("", 15);
        // Cena za 1 kus
        JLabel lblInputPricePerPiece = new JLabel("Cena 1 kus: ");
        txtInputPricePerPiece = new JTextField("", 5);
        // Počet kusů
        JLabel lblInputPieces = new JLabel("Počet kusů: ");
        spInputPieces = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        // Tlačítka
        btnInputAdd = new JButton("Přidat");
        btnInputAdd.addActionListener(this); // Nastavení ActionListeneru - kdo obslouží kliknutí na tlačítko.

        // Přidání komponent do horního panelu pro formulář na přidání položky
        panelInputs.add(lblInputName);
        panelInputs.add(txtInputName);
        panelInputs.add(lblInputPricePerPiece);
        panelInputs.add(txtInputPricePerPiece);
        panelInputs.add(lblInputPieces);
        panelInputs.add(spInputPieces);
        panelInputs.add(btnInputAdd);

        // *** Patička ***
        lblTotalPrice = new JLabel("");
        panelFooter.add(lblTotalPrice, BorderLayout.WEST);

        // *** Tabulka ***
        JTable table = new JTable();
        // Tabulku propojíme s naším modelem
        table.setModel(shoppingCartTableModel);
        // Tabulku přidáme do panelu a obalíme ji komponentou JScrollPane
        panelTable.add(new JScrollPane(table), BorderLayout.CENTER);

        // Přidání (pod)panelů do panelu hlavního
        panelMain.add(panelInputs, BorderLayout.NORTH);
        panelMain.add(panelTable, BorderLayout.CENTER);
        panelMain.add(panelFooter, BorderLayout.SOUTH);

        // Přidání hlavního panelu do MainFrame (JFrame)
        add(panelMain);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Soubor");
        fileMenu.add(new AbstractAction("Nový nákupní seznam") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Nový soubor");
            }
        });
        fileMenu.add(new AbstractAction("Otevřít") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //loadFileXmlSax();
                //loadFileXmlDom();
                loadFileXmlDom4j();
            }
        });
        fileMenu.add(new AbstractAction("Uložit") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveFileCsv();
            }
        });
        fileMenu.add(new AbstractAction("Načti json") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadJson();
            }
        });
        menuBar.add(fileMenu);

        JMenu aboutMenu = new JMenu("O programu");
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }

    // Při kliknutí na jakékoliv tlačítko se zavolá tato metoda.
    // Toho jsme docílili implementování rozhraní ActionListener a nastavením tlačítek např. btnInputAdd.addActionListener(this);
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // Metoda se volá pro každé tlačítko, musíme tedy rozhodnout, co se má skutečně stát pro konkrétní tlačítka
        if (actionEvent.getSource() == btnInputAdd) {
            addProductToCart();
        }
    }

    private void addProductToCart() {
        if (!txtInputName.getText().isBlank()) {
            try {
                double price = Double.parseDouble(txtInputPricePerPiece.getText().replace(",", "."));
                if (price > 0) {
                    // Vytvořit novou položku
                    ShoppingCartItem item = new ShoppingCartItem(txtInputName.getText(), price, (int) spInputPieces.getValue());
                    // Přidat položku do košíku
                    shoppingCart.addItem(item);
                    // Refreshnout tabulku
                    shoppingCartTableModel.fireTableDataChanged();
                    // Upravit patičku
                    updateFooter();

                    // Uložíme do DB
                    dataManager.addShoppingCartItem(item);
                } else {
                    JOptionPane.showMessageDialog(this, "Cena musí být větší než 0", "Chyba", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Zadejte správný formát ceny a počtu kusů!", "Chyba", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Došlo k chybě v programu!", "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vyplňte název produktu!", "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFooter() {
        lblTotalPrice.setText("Celková cena: " + String.format("%.2f", shoppingCart.getTotalPrice()));
    }

    // Metoda, která se postará o uložení košíku do formátu CSV
    private void saveFileCsv() {
        // Init chooser - okno, které Vám dá možnost zvolit umístění souboru a jeho název
        JFileChooser fc = new JFileChooser();
        // Pokud jsme klikli na  tlačítko uložit
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Načeteme si cestu k souboru
            String fileName = fc.getSelectedFile().getAbsolutePath();
            // Try-with-resources https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
            // Nemusíme uzavírat spojení - uzavře se samo
            // Používáme buffer, který potřebuje nějaký primitivnější stream
            try (BufferedWriter bfw = new BufferedWriter(new FileWriter(fileName, true))) {
                // Projdeme položky
                for (ShoppingCartItem item : shoppingCart.getItems()) {
                    // Pro každou položku vytvoříme záznam v CSV
                    bfw.write(item.getName() + ";" + item.getPricePerPiece() + ";" + item.getPieces());
                    // Nový řádek
                    bfw.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Při ukládání došlo k chybě",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Metoda, která načte xml - SAX
    // Mimo olivy můžete kouknout např. sem http://tutorials.jenkov.com/java-xml/sax-defaulthandler.html
    private void loadFileXmlSax() {
        try {
            // Char buffer, do kterého budeme zapisovat "hodnoty" elementů
            CharArrayWriter content = new CharArrayWriter();
            // Vytvoříme SAX parser
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            // Řekneme, který file chceme parsovat a vytvoříme handler, který obslouží události, které budou vznikat při parsování
            parser.parse(new File("src/uhk/fim/shoppingCart.xml"), new DefaultHandler() {
                // Parser narazil na otevřený tag
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    content.reset();
                }

                // Parser narazil na uzavřený tag
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    System.out.println(qName + ": " + content.toString());
                }

                // Parser narazil na nějaký řetězec. Pozor, zavolá se i při nalezení odřádkování.
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    super.characters(ch, start, length);
                    content.write(ch, start, length);
                }
            });

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda, která načte xml - DOM
    private void loadFileXmlDom() {
        try {
            // Vytvoříme builder
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Builder má metodu parse, která se postará o vytvoření objektu docoment, který má v sobě celou strukturu XML
            // Tím jsme si XML uložili do paměti a můžeme s ním dál pracovat = princim DOM
            Document document = builder.parse(new File("src/uhk/fim/shoppingCart.xml"));
            // Z dokumentu načteme prvního potomka = root = kořenová element
            Node root = document.getFirstChild();
            // Ukázka načtení typu nodu.
            short nodeType = root.getNodeType();
            // *** Zde je jen snaha ukázat, že je nutné strukturu projít nějakou rekurzí viz. ukázky v olivě ***
            // Má kořenový element potomky?
            if (root.hasChildNodes()) {
                // Ano má - načteme položky do seznamu
                NodeList list = root.getChildNodes();
                // Projdeme seznam
                for (int i = 0; i < list.getLength(); i++) {
                    // Načteme konkrétní node ze seznamu
                    Node nextNode = list.item(i);
                    // Opět se ptáme, jestli má potomky
                    if (nextNode.hasChildNodes()) {
                        // Ano má - načteme položky do seznamu
                        NodeList list2 = nextNode.getChildNodes();
                        // Projdeme seznam
                        for (int j = 0; j <= list2.getLength(); j++) {
                            // Načteme konkrétní node ze seznamu
                            Node nextNode2 = list2.item(j);
                            /// !!! Tady už můžete vidět, že je ntuné vytvořit nějakou rekurzi !!!
                            // Nemůžeme strukturu takto ručně projít.
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFileXmlDom4j() {
        DocumentFactory df = DocumentFactory.getInstance();
        SAXReader reader = new SAXReader(df);
        try {
            org.dom4j.Document doc = reader.read(new File("src/shoppingCart.xml"));
            System.out.println(doc.asXML());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void loadJson() {
        Gson gson = new Gson();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulace dlouhé odpovědi ze serveru
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    ShoppingCart cart = gson.fromJson(new InputStreamReader(
                            new URL("https://lide.uhk.cz/fim/student/benesja4/shoppingCart.json").openStream()
                    ), ShoppingCart.class);
                    System.out.println("Done");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}