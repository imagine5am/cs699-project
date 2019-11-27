There are three components of the project.
1. Tracking web server
2. Tracking application 
3. Client application (organization)

First of all tracking web server application needs to be hosted.
Say the server address is http://server-address.com

The organization who wants their devices to be tracked needs to register on the portal.
The registration portal for the organization is hosted on link -> http://server-address.com/registration

Once the organization successfully registers/logs in, they receive an API key which is their unique identifier.

Now the tracking application (Android) is installed on the devices which need to be tracked. In this application,
need to enter the API key of their application to register the device. This step is only done for the first time.

In the tracking application, driver can enable/disable sending location details based on their 
working hours.

In the client app (which the organization needs to create), the location details of the drivers registered under the 
organization can be displayed using a map.
