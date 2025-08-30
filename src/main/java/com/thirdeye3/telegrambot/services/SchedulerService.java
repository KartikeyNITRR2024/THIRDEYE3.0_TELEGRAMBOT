package com.thirdeye3.telegrambot.services;

public interface SchedulerService {

	void runToRefreshdata();

	void statusChecker();

	void readAndSendMessage();

}
