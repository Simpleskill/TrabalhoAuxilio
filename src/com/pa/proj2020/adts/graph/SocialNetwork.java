package com.pa.proj2020.adts.graph;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smartgraph.view.graphview.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SocialNetwork extends Application {

    private final DigraphImpl<User, Relation> digraph;
    List<User> users;
    List<Interest> interests;
    List<AuxRelation> auxRelations;
    SmartPlacementStrategy strategy;
    SmartGraphPanel<User, Relation> graphView;
    MementoCareTaker mementoCareTaker;

    public SocialNetwork() {
        digraph = new DigraphImpl<>();
        users = new LinkedList<>();
        this.interests = new LinkedList<>();
        this.auxRelations = new LinkedList<>();
        this.mementoCareTaker = new MementoCareTaker();
    }

    @Override
    public void start(Stage ignored) {

        Path pathUsers = Paths.get("inputFiles/user_names.csv");
        Path absolutePathUsers = pathUsers.toAbsolutePath();
        this.users = importUsers(absolutePathUsers.toString());

        Path pathRelations = Paths.get("inputFiles/relationships.csv");
        Path absolutePathRelations = pathRelations.toAbsolutePath();
        importRelations(absolutePathRelations.toString());

        Path pathInterestsNames = Paths.get("inputFiles/interest_names.csv");
        Path absolutePathInterestsNames = pathInterestsNames.toAbsolutePath();
        importInterestsNames(absolutePathInterestsNames.toString());

        Path pathInterests = Paths.get("inputFiles/interests.csv");
        Path absolutePathInterests = pathInterests.toAbsolutePath();
        importUserInterests(absolutePathInterests.toString());

        Digraph<User, Relation> g = return_digraph();

        this.strategy = new SmartCircularSortedPlacementStrategy();
        this.graphView = new SmartGraphPanel<>(digraph, strategy);
        Scene scene = new Scene(graphView, 1024, 768);


        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Projeto programacao Avancada");
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setScene(scene);
        stage.setX(600);
        stage.setY(0);

        stage.show();
        Stage menu = setUiScreen();
        btnMenuToggle(menu,stage);
        /*
        IMPORTANT: Must call init() after scene is displayed so we can have width and height values
        to initially place the vertices according to the placement strategy
        */
        graphView.init();
        graphView.update();
        /*
        Bellow you can see how to attach actions for when vertices and edges are double clicked
         */

        Stage userInfoStage = new Stage();
        userInfoStage.setX(600);
        userInfoStage.setY(0);
        Stage userInterests = new Stage();
        userInterests.setX(600);
        userInterests.setY(280);


        Stage statisticsStage = new Stage();
        statisticsStage.setX(0);
        statisticsStage.setY(245);
        manageStatistics(statisticsStage);

        graphView.setVertexDoubleClickAction(graphVertex -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().element());

            getUserInfo(graphVertex.getUnderlyingVertex().element(),userInfoStage);

            //toggle different styling
            if (!graphVertex.removeStyleClass("myVertex")) {
                /* for the golden vertex, this is necessary to clear the inline
                   css class. Otherwise, it has priority. Test and uncomment. */
                //graphVertex.setStyle(null);

                graphVertex.addStyleClass("myVertex");
            }

            //want fun? uncomment below with automatic layout
            //g.removeVertex(graphVertex.getUnderlyingVertex());
            //graphView.update();
        });
        graphView.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().element());
            getUserInterests(graphEdge.getUnderlyingEdge(),userInterests);
            //dynamically change the style when clicked
            //graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 2;");


            //uncomment to see edges being removed after click
            //Edge<String, String> underlyingEdge = graphEdge.getUnderlyingEdge();
            //g.removeEdge(underlyingEdge);
            //graphView.update();
        });

        /*
        Should proceed with automatic layout or keep original placement?
        If using SmartGraphDemoContainer you can toggle this in the UI
         */
        //graphView.setAutomaticLayout(true);

        /*
        Uncomment lines to test adding of new elements
         */
        //continuously_test_adding_elements(g, graphView);
        //stage.setOnCloseRequest(event -> {
        //    running = false;
        //});
    }

    /**
     * Method to import all interest names from file given in parameter
     *
     * @param urlFile URL of the file with the information
     */
    private void importInterestsNames(String urlFile) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<User> users = new LinkedList<>();
        try {
            br = new BufferedReader(new FileReader(urlFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] interest = line.split(cvsSplitBy);
                this.interests.add(new Interest(Integer.parseInt(interest[0]), interest[1]));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Digraph return_digraph() {
        return digraph;
    }

     /**
     * Method to import all users from file given in parameter
     *
     * @param urlFile URL of the file with the information
     *
     * @return list with all users
     */
    public List<User> importUsers(String urlFile) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<User> users = new LinkedList<>();
        try {
            br = new BufferedReader(new FileReader(urlFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] user = line.split(cvsSplitBy);

                users.add(new User(Integer.parseInt(user[0]), user[1]));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //     for (User user : users
        //     ) {
        //         digraph.insertVertex(user);
        //     }
        return users;
    }

     /**
     * Method to import all relations from file given in parameter
     *
     * @param urlFile URL of the file with the information
     */
    public void importRelations(String urlFile) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String relations = "";

        try {

            br = new BufferedReader(new FileReader(urlFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] relation = line.split(cvsSplitBy);

                for (int i = 1; i < relation.length; i++) {
                    //     try {
                    //         digraph.insertEdge(getUser(Integer.parseInt(relation[0])), getUser(Integer.parseInt(relation[i])), new Relation(true));
                    //     } catch (InvalidVertexException invalidVertexException) {
                    //     }
                    this.auxRelations.add(new AuxRelation(new Relation(true), getUser(Integer.parseInt(relation[i])), getUser(Integer.parseInt(relation[0]))));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Method restore state
     *
     * @param digraphSet our digraph
     */
    public void restoreState(DigraphImpl<User, Relation> digraphSet) {
        System.out.println(digraphSet.vertices());
        System.out.println(this.digraph.vertices());
        List<Vertex<User>> vertexRemove = new LinkedList<>();
        for (Vertex v : digraph.vertices()
        ) {
            User u1 = (User) v.element();
            vertexRemove.add(v);
            for (Vertex<User> v1 : digraphSet.vertices()
            ) {
                User user = getUser(v1.element().getUserNumber());
                if (u1.getUserNumber() == user.getUserNumber()) {
                    vertexRemove.remove(v);
                }
            }
        }
        for (Vertex<User> v : digraphSet.vertices()
        ) {
            User user = getUser(v.element().getUserNumber());
            if (getUserVertex(user.getUserNumber()) == null) {
                digraph.insertVertex(user);
            }
        }
        for (Edge e : digraphSet.edges()
        ) {
            if (!this.digraph.edges().contains(e)) {
                this.digraph.removeEdge(e);
            }
        }
    }

    /**
     * Method to import all user interests from file given in parameter
     *
     * @param urlFile URL of the file with the information
     *
     * @return list of all interests
     */
    public List<Interest> importUserInterests(String urlFile) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<Interest> interests = new LinkedList<>();
        Interest interest = null;
        try {
            br = new BufferedReader(new FileReader(urlFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] addInterests = line.split(cvsSplitBy);


                for (int i = 1; i < addInterests.length; i++) {
                    try {

                        this.insertInterest(addInterests[0], addInterests[i], "");

                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Invalid exception importing user interest- " + addInterests[i]);
                    }
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return interests;
    }

    /**
     * Method to get a specific user from the users linkedList
     *
     * @param id id of the user
     *
     * @return User
     */
    private User getUser(int id) {

        for (User user : this.users
        ) {
            if (user.getUserNumber() == id) return user;
        }
        return null;
    }

    /**
     * Method to get the vertex from a specific user
     *
     * @param id id of the user that you want to retreive the imformation
     *
     * @return myVertex
     */
    private DigraphImpl.MyVertex getUserVertex(int id) throws InvalidVertexException {

        for (Vertex<User> vertex : digraph.vertices()
        ) {
            DigraphImpl.MyVertex myVertex = digraph.checkVertex(vertex);
            User user = (User) myVertex.getElement();
            if (user.getUserNumber() == id) {
                return myVertex;
            }

        }
        return null;
    }

    /**
     * Method to insert a new interest on one user given in parameter
     *
     * @param newUser User that we want to add the interest
     * @param addInterest the name of the interest
     * @param relationName the name of the relationship
     *
     */
    public void insertInterest(String newUser, String addInterest, String relationName) {
        Interest interest;

        interest = null;
        for (Interest forInterest : this.interests
        ) {
            if (forInterest.identifier == Integer.parseInt(addInterest)) {
                interest = forInterest;
            }
        }
        if (interest == null) {
            interest = new Interest(Integer.parseInt(addInterest), relationName);
            this.interests.add(interest);
        }
        User user = getUser(Integer.parseInt(newUser));
        user.insertInterest(interest); //insert interest user list social network
        //check_new_indirect_relations(user, interest); // check for indirect relations

    }

    /**
     * Method to verify the indirect relations
     *
     * @param newVertex vertex that we want to verify
     * @param interest interest
     *
     */
    public void check_new_indirect_relations(DigraphImpl.MyVertex newVertex, Interest interest) {
        Collection<Vertex<User>> vertices = this.digraph.vertices();

        if (newVertex != null) {
            for (Vertex vertex : vertices
            ) {
                DigraphImpl.MyVertex myVertex = digraph.checkVertex(vertex);
                User user = (User) myVertex.element();
                if (user.getInterestByID(interest.identifier) != null && vertex != newVertex) {
                    try {
                        if (digraph.areAdjacent(vertex, newVertex) || digraph.areAdjacent(newVertex, myVertex)) {

                            List<DigraphImpl.MyEdge> edges = newVertex.getEdges();
                            for (DigraphImpl.MyEdge edge : edges
                            ) {
                                if (edge.vertexInbound == myVertex && edge.vertexOutbound == newVertex) {
                                    Relation relation = (Relation) edge.element();
                                    relation.insertInterest(interest);
                                    break;
                                }

                            }
                            edges = myVertex.getEdges();
                            for (DigraphImpl.MyEdge edge : edges
                            ) {
                                if (edge.vertexInbound == newVertex && edge.vertexOutbound == myVertex) {
                                    Relation relation = (Relation) edge.element();
                                    relation.insertInterest(interest);
                                    break;
                                }

                            }
                        }
                        if (!digraph.areAdjacent(newVertex, myVertex) && !digraph.areAdjacent(myVertex, newVertex)) {
                            Relation newRelation = new Relation(false);
                            newRelation.insertInterest(interest);
                            digraph.insertEdge(newVertex, vertex, newRelation);
                        }
                    } catch (InvalidEdgeException invalidEdgeException) {

                    }
                }
            }
        }
    }

    /**
     * Method to get all relations associated with 1 user
     *
     * @param user user that we want to get the information
     *
     * @return relations List of relations
     *
     */
    public List<AuxRelation> getRelationsUser(User user) {
        List<AuxRelation> relations = new LinkedList<>();
        for (AuxRelation relation : auxRelations
        ) {
            if (relation.outbound == user || relation.inbound == user)
                relations.add(relation);
        }

        return relations;
    }

    public void updateCSS() {
        Collection<Vertex<User>> vertexes = digraph.vertices();

        for (Vertex<User> vertex : vertexes
        ) {
            try {
                User user = vertex.element();
                SmartStylableNode node = graphView.getStylableVertex(user);
                if (user.getUserType() == 'a') {
                    node.setStyle(" -fx-fill : blue ; -fx-stroke : blue");
                } else {
                    node.setStyle(" -fx-fill : green ; -fx-stroke : green");
                }


            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        for (Edge<Relation, User> edge : digraph.edges()
        ) {
            Relation relation = edge.element();
            if (relation.direct) {
                if (relation.interests.size() > 0) {
                    graphView.getStylableEdge(relation).setStyle("-fx-stroke : red");
                    SmartGraphEdgeBase edgeBase = (SmartGraphEdgeBase) graphView.getStylableEdge(relation);
                    SmartArrow smartArrow = edgeBase.getAttachedArrow();
                    smartArrow.setStyle("-fx-stroke : red");
                } else {
                    graphView.getStylableEdge(relation).setStyle("-fx-stroke : black");
                    SmartGraphEdgeBase edgeBase = (SmartGraphEdgeBase) graphView.getStylableEdge(relation);
                    SmartArrow smartArrow = edgeBase.getAttachedArrow();
                    if (smartArrow != null) /* Alterado */
                        smartArrow.setStyle("-fx-stroke : black");
                }
            } else {
                graphView.getStylableEdge(relation).setStyle("-fx-stroke : orange");
                SmartGraphEdgeBase edgeBase = (SmartGraphEdgeBase) graphView.getStylableEdge(relation);
                SmartArrow smartArrow = edgeBase.getAttachedArrow();
                smartArrow.setStyle("-fx-stroke : orange");
            }
        }
    }

    public void btnMenuToggle(Stage menu,Stage primaryStage){
        GridPane grid = new GridPane();
        Button btnMenu = new Button("Menu");
        grid.add(btnMenu, 0, 1);
        //Scene scene = new Scene(grid, 1, 1);
        //primaryStage.setScene(scene);
        btnMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(menu.isShowing())
                {
                    menu.hide();
                }else
                {
                    menu.show();
                }
            }
        });
    }

    int count  = 0;
    public Stage setUiScreen() {

        Stage secondStage = new Stage();
        secondStage.setTitle("Add users:");//300,400,160,200
        secondStage.setMaxHeight(400);
        secondStage.setMaxWidth(500);
        secondStage.setHeight(250);
        secondStage.setWidth(480);
        secondStage.setX(0);
        secondStage.setY(0);
        secondStage.setAlwaysOnTop(true);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        CheckBox automatic = new CheckBox("Automatic layout");
        automatic.selectedProperty().bindBidirectional(graphView.automaticLayoutProperty());
        automatic.setSelected(true);
        grid.add(automatic, 0, 0);
        Scene scene2 = new Scene(grid, 300, 275);
        secondStage.setScene(scene2);
        Button btnFull = new Button("Full model import!");
        HBox hbBtnFull = new HBox(10);
        hbBtnFull.setAlignment(Pos.CENTER_LEFT);
        hbBtnFull.getChildren().add(btnFull);
        grid.add(hbBtnFull, 0, 1);

        btnFull.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                for (User user : users
                ) {
                    count = 0;
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(count*1000);
                                try {
                                    user.userType = 'a';
                                    digraph.insertVertex(user);
                                } catch (Exception ex1) {
                                    System.out.println(ex1);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            updateDiGraph();
                        }
                    }.start();

                    count++;

                }
                try
                {
                    Thread.sleep(count*1000);
                    for (AuxRelation auxrelation : auxRelations
                    ) {
                        count = 0;
                        new Thread()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    Thread.sleep(count*1000);
                                    try {
                                        digraph.insertEdge(auxrelation.outbound, auxrelation.inbound, auxrelation.relation);
                                    } catch (Exception ex1) {
                                        System.out.println(ex1);
                                    }
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                updateDiGraph();
                            }
                        }.start();
                        count++;
                    }
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }


            }
        });

        Label userName = new Label("User ID:");
        grid.add(userName, 1, 0);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button btnInd = new Button("Organize Button!");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER_LEFT);
        hbBtn.getChildren().add(btnInd);
        grid.add(hbBtn, 2, 0);

        btnInd.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                for (Vertex<User> vertex : digraph.vertices()
                ) {
                    if (vertex.element().userNumber == Integer.parseInt(userTextField.getText())) {
                        for (Interest interest : vertex.element().interests
                        ) {
                            check_new_indirect_relations(digraph.checkVertex(vertex), interest);
                        }

                    }
                }

                updateDiGraph();
            }
        });

        Button btn = new Button("Add User!");
        HBox hbBtn1 = new HBox(10);
        hbBtn1.setAlignment(Pos.CENTER_LEFT);
        hbBtn1.getChildren().add(btn);
        grid.add(hbBtn1, 2, 1);

        final Text actionTarget = new Text();
        grid.add(actionTarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                try {
                    try {
                        MementoOriginator mementoOriginator = new MementoOriginator();
                        DigraphImpl memento = mementoOriginator.createMemento(digraph);
                        mementoCareTaker.pushMemento(memento);
                    } catch (Exception ex) {
                        System.out.println("Error" + ex);
                    }

                    for (User user : users
                    ) {
                        if (user.userNumber == Integer.parseInt(userTextField.getText())) {
                            user.userType = 'a';
                            try {
                                digraph.insertVertex(user);
                                actionTarget.setFill(Color.GREEN);
                                actionTarget.setText("User added.");
                            } catch (InvalidVertexException invalidVertexException) {
                                DigraphImpl.MyVertex myVertex = getUserVertex(Integer.parseInt(userTextField.getText()));
                                try {
                                    digraph.replace(myVertex, user);
                                    actionTarget.setFill(Color.GREEN);
                                    actionTarget.setText("Success changing user.");
                                } catch (Exception e1) {
                                    actionTarget.setFill(Color.FIREBRICK);
                                    actionTarget.setText("Error adding user:" + e1.getMessage());
                                }
                            }
                            for (AuxRelation relation : getRelationsUser(user)
                            ) {

                                if (relation.outbound == user) {
                                    System.out.println(relation.inbound.userType);
                                    try {
                                        try {
                                            if (relation.inbound.userType != 'a') {
                                                relation.inbound.userType = 'i';
                                                digraph.insertVertex(relation.inbound);
                                            }

                                        } catch (Exception ex) {

                                        }

                                        digraph.insertEdge(relation.outbound, relation.inbound, relation.relation);
                                        for (AuxRelation relation1 : getRelationsUser(relation.inbound)
                                        ) {
                                            if ((relation1.inbound.userType == 'a' || relation1.outbound.userType == 'a') && relation1.relation.direct)
                                                try {
                                                    digraph.insertEdge(relation1.outbound, relation1.inbound, relation1.relation);
                                                } catch (Exception ex) {
                                                }

                                        }
                                    } catch (InvalidVertexException invalidVertexException) {
                                        System.out.println("Error inserting created relations" + invalidVertexException);
                                    }
                                    // else if (relation.inbound == user) {
                                    //   relation.outbound.userType = 'i';
                                    //   try {
                                    //       digraph.insertVertex(relation.outbound);
                                    //       digraph.insertEdge(relation.inbound, relation.outbound, relation.relation);
                                    //       for (AuxRelation relation1 : getRelationsUser(relation.outbound)
                                    //       ) {
                                    //           if (relation1.inbound.userType == 'a' || relation1.outbound.userType == 'a')
                                    //               digraph.insertEdge(relation1.inbound, relation1.outbound, relation1.relation);
                                    //       }
                                    //   } catch (Exception ex) {
                                    //       System.out.println("Error inserting created relations" + ex);
                                    //   }
                                }
                            }

                            break;
                        }

                    }

                    updateDiGraph();
                } catch (Exception ex) {

                }

            }
        });
        Button btnUndo = new Button("Undo");
        HBox hbBtnUndo = new HBox(10);
        hbBtnUndo.setAlignment(Pos.CENTER_LEFT);
        hbBtnUndo.getChildren().add(btnUndo);
        grid.add(hbBtnUndo, 2, 2);

        btnUndo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                restoreState(mementoCareTaker.popMemento());
                updateDiGraph();
            }
        });
        secondStage.show();
        return secondStage;
    }

    /**
     * Method to show the information of the user on UI, when we click in one vertex
     *
     * @param user user that we want to get the information
     * @param userInfoStage stage where the info is going to be shown
     */
    public void getUserInfo(User user,Stage userInfoStage) {
        userInfoStage.setTitle(user.name.toString());
        userInfoStage.setMaxHeight(400);
        userInfoStage.setMaxWidth(500);
        userInfoStage.setHeight(250);
        userInfoStage.setWidth(480);
        userInfoStage.setAlwaysOnTop(true);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Scene info = new Scene(grid, 0, 0);
        userInfoStage.setScene(info);

        Label lblName = new Label("Name: " + user.name.toString());
        Label lblUserNumber = new Label("User Number: " + user.userNumber);
        Label lblUserType = new Label("User Type: " +(user.userType=='a'?"Adicionado":"Incluido"));
        lblName.setWrapText(true);
        lblUserNumber.setWrapText(true);
        lblUserType.setWrapText(true);
        grid.add(lblName, 0, 0);
        grid.add(lblUserNumber, 0, 1);
        grid.add(lblUserType, 0, 2);

        userInfoStage.show();
    }

    /**
     * Method to show the statistics on UI
     *
     * @param statisticsStage stage where the info is going to be shown
     */
    public void manageStatistics(Stage statisticsStage) {
        statisticsStage.setTitle("Statistic");
        statisticsStage.setMaxHeight(400);
        statisticsStage.setMaxWidth(500);
        statisticsStage.setHeight(350);
        statisticsStage.setWidth(480);
        statisticsStage.setAlwaysOnTop(true);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Scene info = new Scene(grid, 0, 0);
        statisticsStage.setScene(info);

        Label lblUsersTitle = new Label("Users:");
        grid.add(lblUsersTitle, 0, 0);
/*
        for (Vertex v:digraph.vertices()) {
            V secondUser = v.element();
            System.out.println(secondUser.name);
        }

*/
        statisticsStage.show();
    }


    /**
     * Method to show the interests of the user given in parameter, on UI
     *
     * @param edge edge
     * @param userInterests stage where the info is going to be shown
     */
    public void getUserInterests(Edge<Relation, User> edge,Stage userInterests) {
        //Definitions
            DigraphImpl.MyEdge e = digraph.checkEdge(edge);
            Vertex<User> v1 = e.vertexInbound;
            User firstUser = v1.element();
            Vertex<User> v2 = e.vertexOutbound;
            User secondUser = v2.element();
            Relation relation = edge.element();
        //

        userInterests.setTitle(firstUser.name+" vs. "+secondUser.name);
        userInterests.setMaxHeight(400);
        userInterests.setMaxWidth(500);
        userInterests.setHeight(250);
        userInterests.setWidth(480);
        userInterests.setAlwaysOnTop(true);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Scene info = new Scene(grid, 0, 0);
        userInterests.setScene(info);


        Label lblDirect = new Label("The relationship is " +(relation.direct?"direct":"indirect"));

        lblDirect.setStyle("-fx-font-weight: bold;");
        grid.add(lblDirect, 0, 0);

        if(relation.interests.size()==0){
            Label lblTitle = new Label("Have no relations");
            grid.add(lblTitle, 0, 2);
        }else {
            Label lblTitle = new Label("Relations:");
            grid.add(lblTitle, 0, 2);
            int i = 3;
            for (Interest interest : relation.interests) {
                Label lblInterest = new Label(interest.name);
                grid.add(lblInterest, 0, i++);
            }
        }
        userInterests.show();
    }


    private class User {

        private char userType;
        private final int userNumber;
        private final String name;
        private final LinkedList<Interest> interests;

        public User(int userNumber, String name) {
            this.userNumber = userNumber;
            this.interests = new LinkedList<>();
            this.name = name;
        }

        public int getUserNumber() {
            return userNumber;
        }

        public String getName() {
            return name;
        }

        public char getUserType() {
            return userType;
        }

        @Override
        public String toString() {
            return  name;

        }

        /**
         * Method to get all interest
         *
         * @return interests list with all interest
         *
         */
        public LinkedList<Interest> getInterests() {
            return this.interests;
        }

        /**
         * Method to get the interest information
         *
         * @param id if of the interest
         *
         * @return Interest
         *
         */
        public Interest getInterestByID(int id) {

            for (Interest interest : interests
            ) {
                if (interest.identifier == id) {
                    return interest;
                }

            }
            return null;
        }

        /**
         * Method to add a new interest
         *
         * @param addInterest name of the new interest
         *
         */
        public void insertInterest(Interest addInterest) {
            this.interests.add(addInterest);
        }


        /**
         * Method to remove an interest
         *
         * @param removeInterest name of the interest what we want to remove
         *
         */
        public void removeInterest(Interest removeInterest) {
            for (Interest interest : interests
            ) {
                if (interest.identifier == removeInterest.identifier) {
                    interests.remove(interest);
                    return;
                }

            }
        }
    }

    private void updateDiGraph() {
        Runnable r;
        r = () -> {
            graphView.updateAndWait();
            updateCSS();
            graphView.update();
        };
        new Thread(r).start();

    }


    private class AuxRelation {
        private final Relation relation;
        private final User inbound;
        private final User outbound;

        public AuxRelation(Relation relation, User inbound, User outbound) {
            this.relation = relation;
            this.inbound = inbound;
            this.outbound = outbound;
        }

        public Relation getRelation() {
            return relation;
        }

        public User getInbound() {
            return inbound;
        }

        public User getOutbound() {
            return outbound;
        }

    }

    private class Relation {
        boolean direct;
        List<Interest> interests;

        public Relation(boolean direct) {
            this.direct = direct;
            this.interests = new LinkedList<>();

        }

        @Override
        public String toString() {
            return "Relation{" +
                    "direct=" + direct +
                    ", interests=" + interests +
                    '}';
        }

        public void insertInterest(Interest addInterest) {
            this.interests.add(addInterest);
        }
    }

    private class Interest {
        int identifier;
        String name;

        public Interest(int identifier, String name) {
            this.identifier = identifier;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Interest{" +
                    ", identifier=" + identifier +
                    ", name='" + name + "\n" +
                    '}';
        }

        public void set_index(int index) {
            this.identifier = index;
        }

    }

    public class MementoOriginator {

        /**
         * Method create memento of memento pattern
         *
         * @param digraph our digraph
         */
        public DigraphImpl createMemento(DigraphImpl<User, Relation> digraph) {
            DigraphImpl memento = new DigraphImpl();
            for (Object vertex : digraph.vertices()
            ) {
                try {
                    memento.insertVertex(vertex);
                } catch (Exception e) {
                }
            }
            for (Edge<Relation, User> edge : digraph.edges()
            ) {
                try {
                    DigraphImpl.MyEdge myEdge = digraph.checkEdge(edge);
                    memento.insertEdge(myEdge.vertexOutbound, myEdge.vertexInbound, myEdge.element);
                } catch (Exception e) {
                }

            }
            return memento;
        }

        /**
         * Method to get digraph data of memento pattern
         *
         * @param memento our digraph
         */
        public DigraphImpl getDigraphData(DigraphImpl<User, Relation> memento) {
            DigraphImpl mementoR = new DigraphImpl();
            for (Object vertex : memento.vertices()
            ) {
                memento.insertVertex((User) vertex);
            }
            for (Edge<Relation, User> edge : memento.edges()
            ) {
                DigraphImpl.MyEdge myEdge = memento.checkEdge(edge);
                memento.insertEdge(myEdge.vertexOutbound, myEdge.vertexInbound, (Relation) myEdge.element);
            }
            return memento;
        }
    }

    public class MementoCareTaker {
        private final Stack<DigraphImpl> mementoStack = new Stack<>();

        /**
         * Method to push into memento stack (Memento pattern)
         *
         * @param memento our digraph
         */
        public void pushMemento(DigraphImpl memento) {
            mementoStack.push(memento);
            System.out.println("pushed " + memento);
            System.out.println(mementoStack);
        }

        /**
         * Method to pop out of memento stack (Memento pattern)
         *
         * @return digraph
         */
        public DigraphImpl popMemento() {
            System.out.println("poped" + mementoStack.peek());
            return mementoStack.pop();
        }
    }
}
