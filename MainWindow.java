import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


class TravelPackage implements Serializable {


   private String destination;
   private double price;
   private int duration;


   public TravelPackage(String destination, double price, int duration) {
       this.destination = destination;
       this.price = price;
       this.duration = duration;
   }


   public String getDestination() { return destination; }
   public double getPrice() { return price; }
   public int getDuration() { return duration; }


   @Override
   public String toString() {
       return destination + " — $" + price + " — " + duration + " days";
   }
}


class AdventurePackage extends TravelPackage {


   private String activityType;
   private String difficultyLevel;


   public AdventurePackage(String destination, double price, int duration,
                           String activityType, String difficultyLevel) {
       super(destination, price, duration);
       this.activityType = activityType;
       this.difficultyLevel = difficultyLevel;
   }


   public String getActivityType() { return activityType; }
   public String getDifficultyLevel() { return difficultyLevel; }


   @Override
   public String toString() {
       return "[Adventure] " + super.toString()
               + " — " + activityType + " (" + difficultyLevel + ")";
   }
}


class LuxuryPackage extends TravelPackage {


   private String accommodationType;
   private boolean allInclusive;


   public LuxuryPackage(String destination, double price, int duration,
                        String accommodationType, boolean allInclusive) {
       super(destination, price, duration);
       this.accommodationType = accommodationType;
       this.allInclusive = allInclusive;
   }


   public String getAccommodationType() { return accommodationType; }
   public boolean isAllInclusive() { return allInclusive; }


   @Override
   public String toString() {
       return "[Luxury] " + super.toString()
               + " — " + accommodationType
               + (allInclusive ? " (All Inclusive)" : "");
   }
}


class Customer implements Serializable {


   private String name;
   private String email;


   public Customer(String name, String email) {
       this.name = name;
       this.email = email;
   }


   public String getName() { return name; }
   public String getEmail() { return email; }
}


class Booking implements Serializable {


   private Customer customer;
   private TravelPackage travelPackage;


   public Booking(Customer customer, TravelPackage travelPackage) {
       this.customer = customer;
       this.travelPackage = travelPackage;
   }


   public Customer getCustomer() { return customer; }
   public TravelPackage getTravelPackage() { return travelPackage; }


   @Override
   public String toString() {
       return customer.getName() + " → " + travelPackage.getDestination()
               + " ($" + travelPackage.getPrice() + ")";
   }
}


class AgencyManager {


   private ArrayList<TravelPackage> packages;
   private ArrayList<Booking> bookings;
   private HashMap<String, TravelPackage> packageMap;
   private HashMap<String, Integer> packageVisits;


   public AgencyManager() {
       packages = new ArrayList<>();
       bookings = new ArrayList<>();
       packageMap = new HashMap<>();
       packageVisits = new HashMap<>();
   }


   public void addPackage(TravelPackage travelPackage) {
       packages.add(travelPackage);
       packageMap.put(travelPackage.getDestination(), travelPackage);
       packageVisits.put(travelPackage.getDestination(), 0);
   }


   public void addBooking(Booking booking) { bookings.add(booking); }
   public void removeBooking(int bookingIndex) { bookings.remove(bookingIndex); }


   public ArrayList<TravelPackage> getPackages() { return packages; }
   public ArrayList<Booking> getBookings() { return bookings; }


   public void recordPackageClick(String destination) {
       packageVisits.put(destination, packageVisits.getOrDefault(destination, 0) + 1);
   }


   public HashMap<String, Integer> getPackageVisits() {
       return packageVisits;
   }


   public void setPackageVisits(HashMap<String, Integer> visitsData) {
       this.packageVisits = visitsData;
   }


   public ArrayList<TravelPackage> filterPackages(String keyword, double maxPrice) {


       ArrayList<TravelPackage> filteredPackages = new ArrayList<>();


       for (TravelPackage travelPackage : packages) {


           boolean matchesDestination =
                   travelPackage.getDestination().toLowerCase().contains(keyword.toLowerCase());


           boolean matchesPrice =
                   travelPackage.getPrice() <= maxPrice;


           if (matchesDestination && matchesPrice) {
               filteredPackages.add(travelPackage);
           }
       }


       // Sort by destination for binary search capability
       Collections.sort(filteredPackages, Comparator.comparing(TravelPackage::getDestination));


       return filteredPackages;
   }


   public ArrayList<TravelPackage> sortByPrice() {
       ArrayList<TravelPackage> sortedByPrice = new ArrayList<>(packages);
       Collections.sort(sortedByPrice, Comparator.comparingDouble(TravelPackage::getPrice));
       return sortedByPrice;
   }


   public ArrayList<TravelPackage> sortByDuration() {
       ArrayList<TravelPackage> sortedByDuration = new ArrayList<>(packages);
       Collections.sort(sortedByDuration, Comparator.comparingInt(TravelPackage::getDuration));
       return sortedByDuration;
   }


   public TravelPackage getPackageByDestination(String destination) {
       return packageMap.get(destination);
   }
}


class FileManager {


   public static void saveBookings(ArrayList<Booking> bookingsList, String fileName) {


       try (ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(fileName))) {


           out.writeObject(bookingsList);


       } catch (IOException ioException) {
           System.out.println("Error saving: " + ioException.getMessage());
       }
   }


   @SuppressWarnings("unchecked")
   public static ArrayList<Booking> loadBookings(String fileName) {


       ArrayList<Booking> bookingsList = new ArrayList<>();


       try (ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(fileName))) {


           bookingsList = (ArrayList<Booking>) in.readObject();


       } catch (IOException | ClassNotFoundException exception) {
           System.out.println("Error loading: " + exception.getMessage());
       }


       return bookingsList;
   }


   public static void savePackageVisits(HashMap<String, Integer> visitsData, String fileName) {


       try (ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(fileName))) {


           out.writeObject(visitsData);


       } catch (IOException ioException) {
           System.out.println("Error saving visits: " + ioException.getMessage());
       }
   }


   @SuppressWarnings("unchecked")
   public static HashMap<String, Integer> loadPackageVisits(String fileName) {


       HashMap<String, Integer> visitsData = new HashMap<>();


       try (ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(fileName))) {


           visitsData = (HashMap<String, Integer>) in.readObject();


       } catch (IOException | ClassNotFoundException exception) {
           System.out.println("Error loading visits: " + exception.getMessage());
       }


       return visitsData;
   }
}


class PieChartPanel extends JPanel {
   private HashMap<String, Integer> data;
   private Color[] colors = {
           new Color(70, 130, 180),
           new Color(100, 60, 160),
           new Color(70, 160, 100),
           new Color(200, 80, 80),
           new Color(180, 100, 30),
           new Color(160, 120, 50)
   };


   public PieChartPanel(HashMap<String, Integer> visitsData) {
       this.data = visitsData;
       setPreferredSize(new Dimension(300, 250));
       setBackground(new Color(245, 247, 250));
   }


   public void updateData(HashMap<String, Integer> visitsData) {
       this.data = visitsData;
       repaint();
   }


   @Override
   protected void paintComponent(Graphics graphicsContext) {
       super.paintComponent(graphicsContext);
       Graphics2D graphics2D = (Graphics2D) graphicsContext;
       graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


       if (data == null || data.isEmpty() || data.values().stream().allMatch(visit -> visit == 0)) {
           graphics2D.setFont(new Font("Georgia", Font.PLAIN, 14));
           graphics2D.setColor(new Color(160, 170, 185));
           graphics2D.drawString("No package visits yet", 60, 120);
           return;
       }


       int totalVisits = data.values().stream().mapToInt(Integer::intValue).sum();
       double currentStartAngle = 0;
       int colorIndex = 0;


       for (String destination : data.keySet()) {
           int visitCount = data.get(destination);
           double visitPercentage = (double) visitCount / totalVisits;
           double sliceAngle = visitPercentage * 360;


           graphics2D.setColor(colors[colorIndex % colors.length]);
           graphics2D.fillArc(20, 20, 200, 200, (int) currentStartAngle, (int) sliceAngle);
           graphics2D.setColor(Color.WHITE);
           graphics2D.setStroke(new BasicStroke(2));
           graphics2D.drawArc(20, 20, 200, 200, (int) currentStartAngle, (int) sliceAngle);


           currentStartAngle += sliceAngle;
           colorIndex++;
       }


       // Draw legend
       int legendYPosition = 240;
       colorIndex = 0;
       for (String destination : data.keySet()) {
           int visitCount = data.get(destination);
           double visitPercentage = (double) visitCount / totalVisits * 100;


           graphics2D.setColor(colors[colorIndex % colors.length]);
           graphics2D.fillRect(20, legendYPosition - 10, 12, 12);
           graphics2D.setColor(new Color(30, 40, 60));
           graphics2D.setFont(new Font("Georgia", Font.PLAIN, 10));
           String legendLabel = String.format("%s (%.0f%%)", destination, visitPercentage);
           graphics2D.drawString(legendLabel, 40, legendYPosition);


           legendYPosition += 15;
           colorIndex++;
       }
   }
}


public class MainWindow extends JFrame {


   private CardLayout cardLayout;
   private JPanel contentArea;


   private JButton btnHome;
   private JButton btnPackages;
   private JButton btnBookings;


   private AgencyManager manager;
   private DefaultListModel<String> bookingListModel;
   private PieChartPanel pieChartPanel;


   private static final Color SIDEBAR_BG = new Color(30, 40, 60);
   private static final Color ACCENT = new Color(70, 130, 180);
   private static final Color CONTENT_BG = new Color(245, 247, 250);
   private static final Color WHITE = Color.WHITE;
   private static final Color TEXT_DARK = new Color(30, 40, 60);
   private static final Color TEXT_LIGHT = new Color(160, 170, 185);
   private static final Color GREEN = new Color(70, 160, 100);
   private static final Color RED = new Color(200, 80, 80);


   private TravelPackage selectedPackage = null;


   public MainWindow() {


       manager = new AgencyManager();


       manager.addPackage(new AdventurePackage("Bali, Indonesia", 900, 8, "Hiking & Surfing", "Moderate"));
       manager.addPackage(new AdventurePackage("Safari, Kenya", 4200, 12, "Wildlife Safari", "Challenging"));
       manager.addPackage(new LuxuryPackage("Paris, France", 1200, 5, "5-Star Hotel", true));
       manager.addPackage(new LuxuryPackage("Rome, Italy", 1500, 7, "Boutique Hotel", true));
       manager.addPackage(new LuxuryPackage("Tokyo, Japan", 2200, 10, "Luxury Resort", false));
       manager.addPackage(new LuxuryPackage("Maldives", 3500, 7, "Overwater Villa", true));


       bookingListModel = new DefaultListModel<>();


       ArrayList<Booking> loadedBookings = FileManager.loadBookings("bookings.dat");
       HashMap<String, Integer> loadedVisits = FileManager.loadPackageVisits("visits.dat");


       manager.getBookings().clear();
       bookingListModel.clear();


       for (Booking booking : loadedBookings) {
           manager.addBooking(booking);
           bookingListModel.addElement(booking.toString());
       }


       if (!loadedVisits.isEmpty()) {
           manager.setPackageVisits(loadedVisits);
       }


       pieChartPanel = new PieChartPanel(manager.getPackageVisits());


       setTitle("Travel Planner");
       setSize(850, 560);
       setLocationRelativeTo(null);
       setResizable(false);
       setLayout(new BorderLayout());


       setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


       addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent windowEvent) {
               FileManager.saveBookings(manager.getBookings(), "bookings.dat");
               FileManager.savePackageVisits(manager.getPackageVisits(), "visits.dat");
               System.exit(0);
           }
       });


       add(buildSidebar(), BorderLayout.WEST);
       add(buildContentArea(), BorderLayout.CENTER);


       showPanel("HOME");


       setVisible(true);
   }


   private JPanel buildSidebar() {


       JPanel sidebar = new JPanel();
       sidebar.setBackground(SIDEBAR_BG);
       sidebar.setPreferredSize(new Dimension(180, 0));
       sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
       sidebar.setBorder(new EmptyBorder(24, 0, 20, 0));


       JLabel appTitle = new JLabel("Travel Planner");
       appTitle.setFont(new Font("Georgia", Font.BOLD, 16));
       appTitle.setForeground(WHITE);
       appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);


       sidebar.add(appTitle);
       sidebar.add(Box.createVerticalStrut(28));


       btnHome = makeNavButton("Home", "HOME");
       btnPackages = makeNavButton("Packages", "PACKAGES");
       btnBookings = makeNavButton("My Bookings", "BOOKINGS");


       sidebar.add(btnHome);
       sidebar.add(Box.createVerticalStrut(6));
       sidebar.add(btnPackages);
       sidebar.add(Box.createVerticalStrut(6));
       sidebar.add(btnBookings);


       sidebar.add(Box.createVerticalGlue());


       return sidebar;
   }


   private JPanel buildContentArea() {


       cardLayout = new CardLayout();
       contentArea = new JPanel(cardLayout);


       contentArea.setBackground(CONTENT_BG);


       contentArea.add(buildHomePanel(), "HOME");
       contentArea.add(buildPackagesPanel(), "PACKAGES");
       contentArea.add(buildBookingsPanel(), "BOOKINGS");


       return contentArea;
   }


   private JPanel buildHomePanel() {


       JPanel panel = new JPanel(new BorderLayout(0, 20));
       panel.setBackground(CONTENT_BG);
       panel.setBorder(new EmptyBorder(30, 30, 30, 30));


       JLabel welcomeLabel = new JLabel("Welcome back, Mila");
       welcomeLabel.setFont(new Font("Georgia", Font.BOLD, 22));
       welcomeLabel.setForeground(TEXT_DARK);


       JLabel subtitleLabel = new JLabel("What would you like to do today?");
       subtitleLabel.setForeground(TEXT_LIGHT);


       JPanel headerPanel = new JPanel(new BorderLayout());
       headerPanel.setBackground(CONTENT_BG);
       headerPanel.add(welcomeLabel, BorderLayout.NORTH);
       headerPanel.add(subtitleLabel, BorderLayout.SOUTH);


       JPanel middlePanel = new JPanel(new GridLayout(1, 2, 20, 0));
       middlePanel.setBackground(CONTENT_BG);


       JPanel userInfoCard = new JPanel();
       userInfoCard.setLayout(new BoxLayout(userInfoCard, BoxLayout.Y_AXIS));
       userInfoCard.setBackground(WHITE);
       userInfoCard.setBorder(new EmptyBorder(18, 18, 18, 18));


       userInfoCard.add(new JLabel("Your Information"));
       userInfoCard.add(new JLabel("Name: Mila Shteryanova"));
       userInfoCard.add(new JLabel("Email: mila@email.com"));


       JPanel quickActionsCard = new JPanel(new BorderLayout());
       quickActionsCard.setBackground(WHITE);
       quickActionsCard.setBorder(new EmptyBorder(18, 18, 18, 18));


       JPanel quickActionButtonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
       quickActionButtonPanel.setBackground(WHITE);


       JButton browsePackagesButton = makeSimpleButton("Browse Packages", ACCENT);
       JButton myBookingsButton = makeSimpleButton("My Bookings", new Color(100, 60, 160));


       browsePackagesButton.addActionListener(actionEvent -> showPanel("PACKAGES"));
       myBookingsButton.addActionListener(actionEvent -> showPanel("BOOKINGS"));


       quickActionButtonPanel.add(browsePackagesButton);
       quickActionButtonPanel.add(myBookingsButton);


       JPanel statisticsPanel = new JPanel(new BorderLayout());
       statisticsPanel.setBackground(WHITE);
       statisticsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


       JLabel statisticsTitle = new JLabel("Package Visits Statistics");
       statisticsTitle.setFont(new Font("Georgia", Font.BOLD, 14));
       statisticsTitle.setForeground(TEXT_DARK);


       statisticsPanel.add(statisticsTitle, BorderLayout.NORTH);
       statisticsPanel.add(pieChartPanel, BorderLayout.CENTER);


       quickActionsCard.add(quickActionButtonPanel, BorderLayout.NORTH);
       quickActionsCard.add(statisticsPanel, BorderLayout.CENTER);


       middlePanel.add(userInfoCard);
       middlePanel.add(quickActionsCard);


       panel.add(headerPanel, BorderLayout.NORTH);
       panel.add(middlePanel, BorderLayout.CENTER);


       return panel;
   }


   private boolean isValidEmail(String emailAddress) {
       return emailAddress.contains("@") && emailAddress.contains(".com");
   }


   private JPanel buildPackagesPanel() {


       JPanel panel = new JPanel(new BorderLayout());
       panel.setBackground(CONTENT_BG);


       JPanel filterBar = new JPanel();


       JTextField searchField = new JTextField(10);
       JTextField maxPriceField = new JTextField(6);


       JButton filterButton = makeSimpleButton("Filter", ACCENT);
       JButton sortByPriceButton = makeSimpleButton("Sort by Price", new Color(100, 150, 100));
       JButton sortByDurationButton = makeSimpleButton("Sort by Duration", new Color(150, 100, 100));


       filterBar.add(new JLabel("Destination:"));
       filterBar.add(searchField);
       filterBar.add(new JLabel("Max Price:"));
       filterBar.add(maxPriceField);
       filterBar.add(filterButton);
       filterBar.add(sortByPriceButton);
       filterBar.add(sortByDurationButton);


       JPanel packagesGridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
       JScrollPane scrollPane = new JScrollPane(packagesGridPanel);


       Runnable refreshPackagesDisplay = () -> {


           packagesGridPanel.removeAll();


           String searchKeyword = searchField.getText().trim();


           double maximumPrice;
           try {
               maximumPrice = maxPriceField.getText().trim().isEmpty()
                       ? Double.MAX_VALUE
                       : Double.parseDouble(maxPriceField.getText());
           } catch (NumberFormatException numberFormatException) {
               JOptionPane.showMessageDialog(this,
                       "Please enter a valid price.",
                       "Invalid Input",
                       JOptionPane.ERROR_MESSAGE);
               return;
           }


           ArrayList<TravelPackage> filteredPackagesList =
                   manager.filterPackages(searchKeyword, maximumPrice);


           for (TravelPackage travelPackage : filteredPackagesList) {


               JButton packageCard = new JButton();
               packageCard.setLayout(new BoxLayout(packageCard, BoxLayout.Y_AXIS));
               packageCard.setBackground(WHITE);
               packageCard.setFocusPainted(false);
               packageCard.setBorder(BorderFactory.createCompoundBorder(
                       new LineBorder(new Color(220, 225, 235), 1, true),
                       new EmptyBorder(14, 14, 14, 14)
               ));


               JLabel destinationLabel = new JLabel(travelPackage.getDestination());
               JLabel priceLabel = new JLabel("$" + travelPackage.getPrice());
               JLabel durationLabel = new JLabel(travelPackage.getDuration() + " days");


               if (travelPackage instanceof AdventurePackage adventurePackage) {
                   JLabel activityTypeLabel = new JLabel(adventurePackage.getActivityType());
                   JLabel difficultyLevelLabel = new JLabel("Difficulty: " + adventurePackage.getDifficultyLevel());
                   activityTypeLabel.setForeground(new Color(180, 100, 30));
                   difficultyLevelLabel.setForeground(new Color(180, 100, 30));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(activityTypeLabel);
                   packageCard.add(difficultyLevelLabel);
               } else if (travelPackage instanceof LuxuryPackage luxuryPackage) {
                   JLabel accommodationTypeLabel = new JLabel(luxuryPackage.getAccommodationType());
                   JLabel allInclusiveLabel = new JLabel(luxuryPackage.isAllInclusive() ? "All Inclusive" : "Room Only");
                   accommodationTypeLabel.setForeground(new Color(100, 60, 160));
                   allInclusiveLabel.setForeground(new Color(100, 60, 160));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(accommodationTypeLabel);
                   packageCard.add(allInclusiveLabel);
               } else {
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
               }


               packageCard.addActionListener(actionEvent -> {
                   selectedPackage = travelPackage;


                   for (Component component : packagesGridPanel.getComponents()) {
                       if (component instanceof JButton button) {
                           button.setBackground(WHITE);
                       }
                   }


                   packageCard.setBackground(new Color(235, 244, 255));


                   manager.recordPackageClick(travelPackage.getDestination());
                   pieChartPanel.updateData(manager.getPackageVisits());
               });


               packagesGridPanel.add(packageCard);
           }


           packagesGridPanel.revalidate();
           packagesGridPanel.repaint();
       };


       refreshPackagesDisplay.run();
       filterButton.addActionListener(actionEvent -> refreshPackagesDisplay.run());


       sortByPriceButton.addActionListener(actionEvent -> {
           packagesGridPanel.removeAll();
           ArrayList<TravelPackage> pricesSortedList = manager.sortByPrice();
           for (TravelPackage travelPackage : pricesSortedList) {
               JButton packageCard = new JButton();
               packageCard.setLayout(new BoxLayout(packageCard, BoxLayout.Y_AXIS));
               packageCard.setBackground(WHITE);
               packageCard.setFocusPainted(false);
               packageCard.setBorder(BorderFactory.createCompoundBorder(
                       new LineBorder(new Color(220, 225, 235), 1, true),
                       new EmptyBorder(14, 14, 14, 14)
               ));


               JLabel destinationLabel = new JLabel(travelPackage.getDestination());
               JLabel priceLabel = new JLabel("$" + travelPackage.getPrice());
               JLabel durationLabel = new JLabel(travelPackage.getDuration() + " days");


               if (travelPackage instanceof AdventurePackage adventurePackage) {
                   JLabel activityTypeLabel = new JLabel(adventurePackage.getActivityType());
                   JLabel difficultyLevelLabel = new JLabel("Difficulty: " + adventurePackage.getDifficultyLevel());
                   activityTypeLabel.setForeground(new Color(180, 100, 30));
                   difficultyLevelLabel.setForeground(new Color(180, 100, 30));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(activityTypeLabel);
                   packageCard.add(difficultyLevelLabel);
               } else if (travelPackage instanceof LuxuryPackage luxuryPackage) {
                   JLabel accommodationTypeLabel = new JLabel(luxuryPackage.getAccommodationType());
                   JLabel allInclusiveLabel = new JLabel(luxuryPackage.isAllInclusive() ? "All Inclusive" : "Room Only");
                   accommodationTypeLabel.setForeground(new Color(100, 60, 160));
                   allInclusiveLabel.setForeground(new Color(100, 60, 160));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(accommodationTypeLabel);
                   packageCard.add(allInclusiveLabel);
               } else {
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
               }


               packageCard.addActionListener(actionEvent2 -> {
                   selectedPackage = travelPackage;
                   for (Component component : packagesGridPanel.getComponents()) {
                       if (component instanceof JButton button) {
                           button.setBackground(WHITE);
                       }
                   }
                   packageCard.setBackground(new Color(235, 244, 255));


                   manager.recordPackageClick(travelPackage.getDestination());
                   pieChartPanel.updateData(manager.getPackageVisits());
               });


               packagesGridPanel.add(packageCard);
           }
           packagesGridPanel.revalidate();
           packagesGridPanel.repaint();
       });


       sortByDurationButton.addActionListener(actionEvent -> {
           packagesGridPanel.removeAll();
           ArrayList<TravelPackage> durationSortedList = manager.sortByDuration();
           for (TravelPackage travelPackage : durationSortedList) {
               JButton packageCard = new JButton();
               packageCard.setLayout(new BoxLayout(packageCard, BoxLayout.Y_AXIS));
               packageCard.setBackground(WHITE);
               packageCard.setFocusPainted(false);
               packageCard.setBorder(BorderFactory.createCompoundBorder(
                       new LineBorder(new Color(220, 225, 235), 1, true),
                       new EmptyBorder(14, 14, 14, 14)
               ));


               JLabel destinationLabel = new JLabel(travelPackage.getDestination());
               JLabel priceLabel = new JLabel("$" + travelPackage.getPrice());
               JLabel durationLabel = new JLabel(travelPackage.getDuration() + " days");


               if (travelPackage instanceof AdventurePackage adventurePackage) {
                   JLabel activityTypeLabel = new JLabel(adventurePackage.getActivityType());
                   JLabel difficultyLevelLabel = new JLabel("Difficulty: " + adventurePackage.getDifficultyLevel());
                   activityTypeLabel.setForeground(new Color(180, 100, 30));
                   difficultyLevelLabel.setForeground(new Color(180, 100, 30));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(activityTypeLabel);
                   packageCard.add(difficultyLevelLabel);
               } else if (travelPackage instanceof LuxuryPackage luxuryPackage) {
                   JLabel accommodationTypeLabel = new JLabel(luxuryPackage.getAccommodationType());
                   JLabel allInclusiveLabel = new JLabel(luxuryPackage.isAllInclusive() ? "All Inclusive" : "Room Only");
                   accommodationTypeLabel.setForeground(new Color(100, 60, 160));
                   allInclusiveLabel.setForeground(new Color(100, 60, 160));
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
                   packageCard.add(accommodationTypeLabel);
                   packageCard.add(allInclusiveLabel);
               } else {
                   packageCard.add(destinationLabel);
                   packageCard.add(priceLabel);
                   packageCard.add(durationLabel);
               }


               packageCard.addActionListener(actionEvent2 -> {
                   selectedPackage = travelPackage;
                   for (Component component : packagesGridPanel.getComponents()) {
                       if (component instanceof JButton button) {
                           button.setBackground(WHITE);
                       }
                   }
                   packageCard.setBackground(new Color(235, 244, 255));


                   manager.recordPackageClick(travelPackage.getDestination());
                   pieChartPanel.updateData(manager.getPackageVisits());
               });


               packagesGridPanel.add(packageCard);
           }
           packagesGridPanel.revalidate();
           packagesGridPanel.repaint();
       });


       JPanel bottomPanel = new JPanel();


       JTextField customerNameField = new JTextField(10);
       JTextField customerEmailField = new JTextField(10);
       JButton bookButton = makeSimpleButton("Book", ACCENT);


       bookButton.addActionListener(actionEvent -> {


           if (selectedPackage == null) {
               JOptionPane.showMessageDialog(this,
                       "Please select a package.",
                       "No Package Selected",
                       JOptionPane.WARNING_MESSAGE);
               return;
           }


           if (customerNameField.getText().isBlank()) {
               JOptionPane.showMessageDialog(this,
                       "Please enter your name.",
                       "Empty Name",
                       JOptionPane.ERROR_MESSAGE);
               return;
           }


           if (customerEmailField.getText().isBlank()) {
               JOptionPane.showMessageDialog(this,
                       "Please enter your email.",
                       "Empty Email",
                       JOptionPane.ERROR_MESSAGE);
               return;
           }


           if (!isValidEmail(customerEmailField.getText())) {
               JOptionPane.showMessageDialog(this,
                       "Please enter a valid email (must contain @ and .com).",
                       "Invalid Email",
                       JOptionPane.ERROR_MESSAGE);
               return;
           }


           Booking newBooking = new Booking(
                   new Customer(customerNameField.getText(), customerEmailField.getText()),
                   selectedPackage
           );


           manager.addBooking(newBooking);
           bookingListModel.addElement(newBooking.toString());


           JOptionPane.showMessageDialog(this,
                   "Booking successful!\n\n" + newBooking.toString(),
                   "Success",
                   JOptionPane.INFORMATION_MESSAGE);


           customerNameField.setText("");
           customerEmailField.setText("");
           selectedPackage = null;
       });


       bottomPanel.add(new JLabel("Name:"));
       bottomPanel.add(customerNameField);
       bottomPanel.add(new JLabel("Email:"));
       bottomPanel.add(customerEmailField);
       bottomPanel.add(bookButton);


       panel.add(filterBar, BorderLayout.NORTH);
       panel.add(scrollPane, BorderLayout.CENTER);
       panel.add(bottomPanel, BorderLayout.SOUTH);


       return panel;
   }


   private JPanel buildBookingsPanel() {


       JPanel panel = new JPanel(new BorderLayout());


       JList<String> bookingsList = new JList<>(bookingListModel);


       JButton deleteButton = makeSimpleButton("Delete", RED);
       JButton updateButton = makeSimpleButton("Update", ACCENT);


       deleteButton.addActionListener(actionEvent -> {
           int selectedIndex = bookingsList.getSelectedIndex();
           if (selectedIndex == -1) {
               JOptionPane.showMessageDialog(this,
                       "Please select a booking to delete.",
                       "No Selection",
                       JOptionPane.WARNING_MESSAGE);
               return;
           }


           int userChoice = JOptionPane.showConfirmDialog(this,
                   "Are you sure you want to delete this booking?",
                   "Confirm Delete",
                   JOptionPane.YES_NO_OPTION);


           if (userChoice == JOptionPane.YES_OPTION) {
               manager.removeBooking(selectedIndex);
               bookingListModel.remove(selectedIndex);
           }
       });


       updateButton.addActionListener(actionEvent -> {


           int selectedIndex = bookingsList.getSelectedIndex();
           if (selectedIndex == -1) {
               JOptionPane.showMessageDialog(this,
                       "Please select a booking to update.",
                       "No Selection",
                       JOptionPane.WARNING_MESSAGE);
               return;
           }


           Booking selectedBooking = manager.getBookings().get(selectedIndex);


           JTextField updateNameField =
                   new JTextField(selectedBooking.getCustomer().getName());


           JTextField updateEmailField =
                   new JTextField(selectedBooking.getCustomer().getEmail());


           JTextField updateDestinationField =
                   new JTextField(selectedBooking.getTravelPackage().getDestination());


           JPanel updateFormPanel = new JPanel(new GridLayout(3, 2));


           updateFormPanel.add(new JLabel("Name:"));
           updateFormPanel.add(updateNameField);


           updateFormPanel.add(new JLabel("Email:"));
           updateFormPanel.add(updateEmailField);


           updateFormPanel.add(new JLabel("Destination:"));
           updateFormPanel.add(updateDestinationField);


           int dialogResult = JOptionPane.showConfirmDialog(
                   this,
                   updateFormPanel,
                   "Update Booking",
                   JOptionPane.OK_CANCEL_OPTION
           );


           if (dialogResult == JOptionPane.OK_OPTION) {


               if (updateNameField.getText().isBlank()) {
                   JOptionPane.showMessageDialog(this,
                           "Name cannot be empty.",
                           "Empty Name",
                           JOptionPane.ERROR_MESSAGE);
                   return;
               }


               if (updateEmailField.getText().isBlank()) {
                   JOptionPane.showMessageDialog(this,
                           "Email cannot be empty.",
                           "Empty Email",
                           JOptionPane.ERROR_MESSAGE);
                   return;
               }


               if (!isValidEmail(updateEmailField.getText())) {
                   JOptionPane.showMessageDialog(this,
                           "Please enter a valid email (must contain @ and .com).",
                           "Invalid Email",
                           JOptionPane.ERROR_MESSAGE);
                   return;
               }


               TravelPackage originalPackage = selectedBooking.getTravelPackage();


               TravelPackage updatedPackage;


               if (originalPackage instanceof AdventurePackage adventurePackage) {
                   updatedPackage = new AdventurePackage(
                           updateDestinationField.getText(),
                           originalPackage.getPrice(),
                           originalPackage.getDuration(),
                           adventurePackage.getActivityType(),
                           adventurePackage.getDifficultyLevel()
                   );
               } else if (originalPackage instanceof LuxuryPackage luxuryPackage) {
                   updatedPackage = new LuxuryPackage(
                           updateDestinationField.getText(),
                           originalPackage.getPrice(),
                           originalPackage.getDuration(),
                           luxuryPackage.getAccommodationType(),
                           luxuryPackage.isAllInclusive()
                   );
               } else {
                   updatedPackage = new TravelPackage(
                           updateDestinationField.getText(),
                           originalPackage.getPrice(),
                           originalPackage.getDuration()
                   );
               }


               Booking updatedBooking = new Booking(
                       new Customer(
                               updateNameField.getText(),
                               updateEmailField.getText()
                       ),
                       updatedPackage
               );


               manager.getBookings().set(selectedIndex, updatedBooking);
               bookingListModel.set(selectedIndex, updatedBooking.toString());
           }
       });


       JPanel buttonControlPanel = new JPanel();
       buttonControlPanel.add(updateButton);
       buttonControlPanel.add(deleteButton);


       panel.add(new JScrollPane(bookingsList), BorderLayout.CENTER);
       panel.add(buttonControlPanel, BorderLayout.SOUTH);


       return panel;
   }
   // =======================================================


   private void showPanel(String panelName) {


       cardLayout.show(contentArea, panelName);


       for (JButton navigationButton : new JButton[]{btnHome, btnPackages, btnBookings}) {
           if (navigationButton != null) {
               navigationButton.setBackground(SIDEBAR_BG);
               navigationButton.setForeground(TEXT_LIGHT);
           }
       }


       JButton activeNavButton = switch (panelName) {
           case "HOME" -> btnHome;
           case "PACKAGES" -> btnPackages;
           default -> btnBookings;
       };


       activeNavButton.setBackground(ACCENT);
       activeNavButton.setForeground(WHITE);
   }


   private JButton makeNavButton(String buttonLabel, String targetPanelName) {
       JButton navigationButton = new JButton(buttonLabel);
       navigationButton.setForeground(TEXT_LIGHT);
       navigationButton.setBackground(SIDEBAR_BG);
       navigationButton.setFocusPainted(false);
       navigationButton.addActionListener(actionEvent -> showPanel(targetPanelName));
       return navigationButton;
   }


   private JButton makeSimpleButton(String buttonText, Color backgroundColor) {
       JButton simpleButton = new JButton(buttonText);
       simpleButton.setBackground(backgroundColor);
       simpleButton.setForeground(WHITE);
       simpleButton.setFocusPainted(false);
       return simpleButton;
   }


   public static void main(String[] args) {
       SwingUtilities.invokeLater(MainWindow::new);
   }
}
