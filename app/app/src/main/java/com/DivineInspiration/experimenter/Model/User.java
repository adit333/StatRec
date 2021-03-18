package com.DivineInspiration.experimenter.Model;

import org.jetbrains.annotations.NotNull;

public class User {
    private String userId;
    private String userName;
    private String description;
    private UserContactInfo contactInfo;

    /**
     * User contructor
     * @param username
     * name of the person
     * @param contactUserInfo
     * contact info on that person
     */
    public User(String username, String userId, UserContactInfo contactUserInfo, String description) {
        this.userId = userId;
        this.userName = username;
        this.contactInfo = contactUserInfo;
        this.description = description;
    }
    public User(String userId){
        this.userId = userId;
        userName = "UserName";
        contactInfo = new UserContactInfo();
        description = "";

    }

    public User(){
        userId = "defaultId";
        userName = "UserName";
        contactInfo = new UserContactInfo();
        description = "";
    }

    /**
     * Gets contact info of user
     * @return
     * Contact person class of user
     */
    public UserContactInfo getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets new contact info of person
     * @param contactInfo
     * new contact info
     */
    public void setContactInfo(UserContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Gets name of user
     * @return
     * Username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets username
     * @param userName
     * User name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets ID of user
     * @return
     * User ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Creates a new experiment under user => move to owner?
     * @param experimentName
     * @param experimentDescription
     */
    public void createExperiment(String experimentName, String experimentDescription){
        String experimentOwnerID = getUserId();
        new Experiment(experimentName, this, experimentDescription);
    }

    /**
     * Subscribes to an experiment
     * @param experiment
     * experiment to subscribe to
     */
    public void subscribeExperiment(Experiment experiment){
        String subscriberID = getUserId();
        experiment.addSubscriber(this);
    }

    /**
     * Unsubscribes an experiment
     * @param experiment
     * experiment to unsubscribe to
     */
    public void unsubscribeExperiment(Experiment experiment){
        String subscriberID = getUserId();
        experiment.deleteSubscriber(this);
    }

    @NotNull
    @Override
    public String toString(){
        return String.format("User Name: %s, Id: %s", userName, userId);
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String newDescription){
        this.description = newDescription;
    }
}
