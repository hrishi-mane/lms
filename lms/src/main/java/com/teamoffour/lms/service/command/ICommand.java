package com.teamoffour.lms.service.command;

/**
 * Command Pattern Interface
 */
public interface ICommand {
    String execute();
    String undo();
    String getDescription();
}