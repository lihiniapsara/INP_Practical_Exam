package org.example.controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientController1 {
    @FXML
    private TextField txtMessage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageVBox;

    @FXML
    private Button btnSend;

    @FXML
    private Button btnImage;

    @FXML
    private TabPane emojiTabPane;

    @FXML
    private Button btnEmoji;

    @FXML
    private VBox emojiPickerBox;

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";

    private final String[][] EMOJIS = {
            // Smileys
            {"😀", "😃", "😄", "😁", "😅", "😂", "🤣", "😊", "😇", "🙂", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜"},
            // Hearts & Emotions
            {"❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔", "❤️‍🔥", "💘", "💝", "💖", "💗", "💓", "💞", "💕", "💫", "💥", "💢", "💤"},
            // Animals
            {"🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔", "🐧", "🐦", "🦆", "🦅", "🦉", "🦇"},
            // Food & Drinks
            {"🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🥑", "🍔", "🍕", "🌭", "🍿", "🧂"},
            //transport
            {"🚗", "🚓", "🚕", "🛺", "🚙", "🛻", "🚌", "🚐", "🚎", "🚑", "🚚", "🚍", "🚔", "🚘", "🚜", "🚛", "🦽", "🦼", "🛹", "🛼", "🚲", "🛴","🛟","🚂"}

    };

    private final String[] CATEGORIES = {"Smileys", "Hearts", "Animals", "Food", "Transport"};

    @FXML
    public void initialize() {
        try {
            initializeEmojiPicker();
            emojiPickerBox.setVisible(false);

            if (messageVBox != null) {
                messageVBox.setSpacing(10);
                scrollPane.setContent(messageVBox);
                scrollPane.setFitToWidth(true);

                messageVBox.heightProperty().addListener((observable, oldValue, newValue) ->
                        scrollPane.setVvalue(1.0));
            } else {
                System.err.println("Error : VBox is Null");
                return;
            }

            new Thread(() -> {
                try {
                    socket = new Socket("localhost", 4000);
                    Platform.runLater(() -> addMessage("Client Connected"));
                    Platform.runLater(() -> addMessage("Client1 Connected"));

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (!message.equals("Exit")) {
                        message = dataInputStream.readUTF();

                        if (message.startsWith("[IMAGE]")) {
                            String imagePath = message.substring(7);
                            Platform.runLater(() -> {
                                addMessage("Server:");

                                displayImage(imagePath);
                            });
                        } else {
                            Platform.runLater(() -> addMessage("Server: " + message));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> addMessage("Error: Server not found "));
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeEmojiPicker() {
        for (int i = 0; i < CATEGORIES.length; i++) {
            Tab tab = new Tab(CATEGORIES[i]);
            FlowPane flowPane = new FlowPane();
            flowPane.setHgap(5);
            flowPane.setVgap(5);
            flowPane.setPrefWrapLength(200);

            for (String emoji : EMOJIS[i]) {
                Button emojiButton = new Button(emoji);
                emojiButton.setStyle("-fx-font-size: 20px; -fx-background-color: #e9f10a;");
                emojiButton.setOnAction(e -> {
                    txtMessage.appendText(emoji);
                    emojiPickerBox.setVisible(false);
                });
                flowPane.getChildren().add(emojiButton);
            }

            tab.setContent(flowPane);
            emojiTabPane.getTabs().add(tab);
        }
    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {
        emojiPickerBox.setVisible(!emojiPickerBox.isVisible());
    }

    private void addMessage(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;");
        label.setWrapText(true);
        messageVBox.getChildren().add(label);
    }

    private void displayImage(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            messageVBox.getChildren().add(imageView);
        } catch (Exception e) {
            addMessage("Error: loading image");
            e.printStackTrace();
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        try {
            String message = txtMessage.getText().trim();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                addMessage("Client1: " + message);
                txtMessage.clear();
            }
        } catch (IOException e) {
            addMessage("Error: Failed message");
            e.printStackTrace();
        }
    }

    @FXML
    void btnImageOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            addMessage("Client1:");

            try {
                displayImage(selectedFile.getPath());
                dataOutputStream.writeUTF("[IMAGE]" + selectedFile.getPath());
                dataOutputStream.flush();
            } catch (IOException e) {
                addMessage("Error: Failed image");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }
}
