package me.cursedblackcat.projectcrumbs;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class ProjectCrumbs extends Application {	
	DiscordApi api;

	public static void main(String[] args){
		launch(args);
	}
	
	public DiscordApi login(String token) throws Exception {
		token = token.replaceAll("\"", "");
		
		api =  new DiscordApiBuilder().setAccountType(AccountType.CLIENT).setToken(token).login().join();

		api.addMessageCreateListener(event -> {
			if (event.getMessage().getContent().equalsIgnoreCase("!ping")) {
				System.out.println("Pong!");
			}
		});

		System.out.println("Logged in as " + api.getYourself().getDiscriminatedName());
		
		return api;
	}
	
	public Scene loginScreen() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Login");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label tokenLabel = new Label("Discord Token:");
		grid.add(tokenLabel, 0, 1);

		TextField tokenTextField = new TextField();
		grid.add(tokenTextField, 1, 1);
		
		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 2);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
					api = login(tokenTextField.getText());
					new Alert(AlertType.INFORMATION, "Successfully logged in as " + api.getYourself().getDiscriminatedName()).showAndWait();
				} catch (Exception e) {
					new Alert(AlertType.ERROR, "Invalid token.").showAndWait();
					tokenTextField.setText("");
				}
            }
        });
		
		return new Scene(grid, 300, 275);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Project Crumbs");
		primaryStage.setScene(loginScreen());
		primaryStage.show();
	}
}
