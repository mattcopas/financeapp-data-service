# README #

[![Build Status](https://travis-ci.org/mattcopas/financeapp-data-service.svg?branch=add-oauth2-security)](https://travis-ci.org/mattcopas/financeapp-data-service)

### What is this repository for? ###

* The app provides a REST API for creating and managing financial accounts
* Version 0.0.1

### How do I get set up? ###

## Authentication ##
Send a request:
curl -X POST
    --user 'financeapp:secret'
    -d 'grant_type=password&username=test@test.com&password=password'
    http://localhost:8081/oauth/token

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Please raise an issue if you have any questions
