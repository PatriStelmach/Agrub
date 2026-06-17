# AlertWIP

## Overview

AlertVIP for mobile is an application built in flutter for the purpose of monitoring and notifying about active server events for use with the AlertVIP server agent.

## Getting Started

### Prerequisites

- flutter
- dart

### Setup project

- Clone this repository using `git clone https://github.com/PatriStelmach/AlertVIP`
- cd into mobile_app
- `flutter pub` get to get all the dependencies
- Generate files using Builder Runner (required)
```
flutter pub run build_runner build
```
Make sure you have a connected Android/iOS device/simulator and run the following command to build and run the app in debug mode.

```
flutter run
```

## Features:

### Alerts:
- Active alert monitoring
- Alert History viewing with sorting
- ACKing and commenting alerts
- Sound notifying on highest alert levels

### Scripts:
- Manual script running
- Modifying scheduled scripts

### Profiles:
- Viewing your profile 

### Multi language support:
- Polish 
- English


## Contributors

- Patri Stelmach
- Kamil Kornatowski
- Błażej Majchrzak
