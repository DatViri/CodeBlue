# CodeBlue
An android application that reduces manual work for nurses

## The what
In hospital, nurses usually check patient's health status daily, which includes heart rate. The problem is that not all of patients need to use Electrocardiogram (EGC) machine. Therefore, nurses need to manually use handheld sphygmomanometer device to measure patients' heart rates. That process takes time and effort from both nurses and patients, and it's boring.

## The how
- Attach a heart measurement sensor on the patient's arm with an armband. In the scope of this project, we use Polar OH1 sensor
![Polar OH1](https://www.polar.com/sites/default/files/product2/intro/Polar-OH1-Optical-Heartrate-sensor_13.png)
  ***Why Polar OH1?***
  
  *Polar provides open API that allows developers to get information of the users*
- Associate the sensor's MAC address to a barcode/image/user
- Make an application that scans the barcode and connect to the sensor associated to that barcode automatically

  ***Why not choose and connect the device that is on the patient arm at the first place?***
  
  *In a hospital, there are many patients. Each of them has his/her own sensor. How does a nurse possibly know which is the right one to connect?*

## The detail

### How to use the app
You open the application, there will be an instruction of how to use the app (it only shows up the first time you open the app). Detail about steps:
- Turn on the sensor and attach it to the patient's arm
- Scan the patient image/barcode
- Once the app detect the image/barcode, it navigates you to the info screen where you can see 
data related to the patient's health (BMI, heart rate etc), which comes from both the sensor and 
Polar API

### Dependencies
- Polar API

### Libraries
- **Retrofit** Making http requests and handle responses
- **Rxjava** The reactive way to handle multi-thread and data flow of app
- **Room** Internal database to persist data received from service
- **Android Lifecycle libs** Provides viewmodel and livedata
- **Koin** A Kotlin framework that helps dependency injection easier
- **MPAndroidChart** Chart and analytics stuff
- **ARCore** Detect user image/barcode
- **Support libraries** views and stuff related to android
- **Google Play Services** Request for GPS stuff

### Architecture
The application, or at least part of it, follows clean architecture by Uncle Bob

![Architecture](https://camo.qiitausercontent.com/5e3b5ed8f718b90c7075e434ab3f05e596bcbf23/68747470733a2f2f7261772e67697468756275736572636f6e74656e742e636f6d2f616e64726f696431302f53616d706c652d446174612f6d61737465722f416e64726f69642d436c65616e4172636869746563747572652f636c65616e5f6172636869746563747572655f6c61796572735f64657461696c732e706e67)
