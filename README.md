# $${\color{gold}Welcome}$$ $${\color{white}to}$$ $${\color{lightblue}Blodged}$$

This project is a continuation of the group application built in CST-339 (Java III) at Grand Canyon University. My goal for this continuation is to update the current structure to fit a communication website specifictally for coders with real-time updates on post, replies, and follows! I also plan to add daily coding challenges and other fun features to keep users intertained!

## Frontend Layout
```
Source/Frontend/
  public/
  src/
    assets/
    components/
      icons/
      settings/
      NavBar.vue
      HelloWorld.vue
      UserCard.vue
      Reply.vue
      RepliesSection.vue
      WelcomeItem.vue
      TheWelcome.vue
    pages/
      Home.vue
      Profile.vue
      PostView.vue
      Login.vue
      Register.vue
      Settings.vue
      CreatePost.vue
      NotFound.vue
      Messages.vue
      Admin.vue
    router/
      index.ts
    store/
      user.ts
    api/
      index.ts
    App.vue
    main.ts
    shims-vue.d.ts
  index.html
  package.json
  tsconfig.json
  vite.config.ts
```

## Backend Layout
```
Source/Backend/
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── blodged/
    │   │           ├── BackendApplication.java
    │   │           ├── business/
    │   │           ├── config/
    │   │           ├── controller/
    │   │           ├── data/
    │   │           ├── model/
    │   └── resources/
    │       ├── application.properties
    │       ├── static/
    │       └── templates/
    └── test/
        └── java/
            └── com/
                └── blodged/
```

## Goals
- Remove exsisting html and replace frontend with Typescript or Vue.js
- Update UI to a more modern, coding look
- ~~Implement the exsisting Spring Boot Java as the backend~~

## To-Do
- Change login/register page to use a modal ontop of the home page
- Deploy SQL database to cloud
- Change UI Theme
- Add settings Page\
`(find more details in the links below)`
---
### Check out the [User Stories](Documents/Requirements/User-Stories.md)
### Check out the [Functional Requirements](Documents/Requirements/FunctionalRequirements.md)
### Check out the [NonFunctional Requirements](Documents/Requirements/NonFunctionalRequirements.md)


![Blodged Logo](Documents/Images/Logo/Blodged_Trans.png)
