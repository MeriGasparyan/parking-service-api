# Parking Service API Documentation

## Table of Contents
- [Overview](#overview)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
    - [Authentication Controller](#authentication-controller)
    - [Community Controller](#community-controller)
    - [Parking Controller](#parking-controller)
    - [Spot Controller](#spot-controller)
    - [User Controller](#user-controller)
- [Data Models](#data-models)
    - [Booking](#booking)
    - [Spot](#spot)
    - [Community](#community)
    - [User](#user)
- [Permissions](#permissions)
- [Business Rules](#business-rules)
- [Error Handling](#error-handling)

## Overview
A comprehensive RESTful API for managing parking spots, bookings, communities, and user authentication in a parking management system. The API implements role-based access control with various permissions.

## Authentication
The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header for all authenticated requests


## API Endpoints

### Authentication Controller

#### Login
`POST /api/auth/login`

Authenticates a user and returns a JWT token.

Does **not** require any authentication or permissions.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securepassword123"
}
```

### Community Controller

#### Get Community Spots
`GET /api/communities/{communityId}/spots`

Get all parking spots for a specific community.
**Required Permission:** `VIEW_AVAILABLE_SPOT`

#### Get Community
`GET /api/communities/{id}`

Get community details by ID.

#### Create Community
`POST /api/communities`
Create a new community. May not include Community manager, can be added via separate request.
**Required Permission:** `CREATE_COMMUNITY`

**Request Body:**
```json
{
  "name": "New Community",
  "address": "456 Oak Ave"
}
```

#### Update Community
`PUT /api/communities/{id}`

Update a community.

**Required Permission:** `UPDATE_COMMUNITY`

**Request Body:**
```json
{
  "name": "Updated Community Name",
  "address": "Updated Address"
}
```

#### Delete Community
`DELETE /api/communities/{id}`

Delete a community.

**Required Permission:** `DELETE_COMMUNITY`

### Parking Controller

#### Create Booking
`POST /api/parking/book`

Create a new parking booking for a specific user(authentication via jwt token)

**Required Permission:** `CREATE_BOOKING`

**Request Body:**
```json
{
  "spotId": 1,
  "startTime": "2023-06-15T09:00:00Z",
  "endTime": "2023-06-15T11:00:00Z"
}
```

#### Create Guest Booking
`POST /api/parking/guest-book`

Create a guest parking booking. 
This is useful for creating short-term(configure duration in `application.properties`) bookings in none another community (i.e. in community not listed in User profile.)

**Required Permission:** `CREATE_GUEST_BOOKING`

**Request Body:** Same as regular booking

#### Get Available Spots
`GET /api/parking/{communityId}/available-spots?from={datetime}&to={datetime}`

Get available spots in a community for a given time range.

**Required Permission:** `VIEW_AVAILABLE_SPOT`

### Spot Controller

#### Create Spot
`POST /api/spots`

Create a new parking spot.

**Required Permission:** `CREATE_SPOT`

**Request Body:**
```json
{
  "code": "B-202",
  "address": "Near Elevator",
  "spotType": "RESIDENT",
  "communityId": 1
}
```

#### Create Spot
`PUT /api/spots/{id}`

Create a new parking spot. Note for community managers they can create new spots only in their community.

**Required Permission:** `CREATE_SPOT`

**Request Body:**
```json
{
  "code": "B-202",
  "address": "Near Elevator",
  "spotType": "RESIDENT",
  "communityId": 1
}
```
#### Update Spot
`PUT /api/spots/{id}`

Update a parking spot. Note for community managers they can modify spots only in their community.

**Required Permission:** `UPDATE_SPOT`

**Request Body:**
```json
{
  "code": "B-202",
  "address": "Near Elevator",
  "spotType": "STANDARD",
  "communityId": 1
}
```
Note: Not all fields are required to fill.

#### Update Spot
`DELETE /api/spots/{id}`

Update a parking spot. Note for community managers they can delete spots only in their community.

**Required Permission:** `DELETE_SPOT`

### User Controller

#### Create User
`POST /api/users/community/{communityId}`

Create a new user in a community.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1234567890",
  "password": "securepassword123",
}
```
On default all users are created with role `ROLE_RESIDENT`. The role updates should be done later by admin.

#### Create Admin
`POST /api/users/create-admin`

Create an admin. This endpoint does not require authentication and only exists for testing purposes. Admin is not linked to a specific community.

**Request Body:** Same as regular user creation.

## Data Models

### Booking
```json
{
  "id": "long",
  "userId": "long",
  "spotId": "long",
  "startTime": "ISO-8601 datetime",
  "endTime": "ISO-8601 datetime",
  "status": "enum[BOOKED, IN_PROGRESS, PARKED, COMPLETED, CANCELLED]"
}
```

### Spot
```json
{
  "id": "long",
  "code": "string",
  "address": "string",
  "spotType": "enum[RESIDENT, VISITOR, HANDICAPPED]",
  "communityId": "long"
}
```

### Community
```json
{
  "id": "long",
  "name": "string",
  "address": "string",
  "communityManagerId": "long"
}
```

### User
```json
{
  "id": "long",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phoneNumber": "string",
  "communityId": "long",
  "role": "enum[ADMIN, COMMUNITY_MANAGER, RESIDENT, VISITOR]"
}
```

## Permissions
| Permission | Description |
|------------|-------------|
| VIEW_AVAILABLE_SPOT | View available parking spots |
| CREATE_COMMUNITY | Create new communities |
| UPDATE_COMMUNITY | Update existing communities |
| DELETE_COMMUNITY | Delete communities |
| CREATE_BOOKING | Create regular bookings |
| CREATE_GUEST_BOOKING | Create guest bookings |
| VIEW_ALL_BOOKINGS | View all bookings in system |
| VIEW_OWN_BOOKING | View only own bookings |
| VIEW_CURRENT_BOOKINGS | View currently active bookings |
| PARK_IN_SPOT | Mark parking as in progress |
| RELEASE_SPOT | Release a parking spot |
| CANCEL_BOOKING | Cancel a booking |
| CHANGE_BOOKING_STATUS | Modify booking status |
| CREATE_SPOT | Create parking spots |
| UPDATE_SPOT | Update parking spots |
| DELETE_SPOT | Delete parking spots |

## Business Rules
1. **Booking Restrictions**:
    - Regular users can only book spots within their own community
    - Visitor spots can only be booked through guest booking endpoint
    - Guest bookings have maximum duration limit (default: 120 minutes)

2. **Spot Management**:
    - Only community managers can manage spots in their community
    - Visitor spots must be of type VISITOR

3. **Booking Status Flow**:
   ```
   BOOKED → PARKED → IN_PROGRESS → COMPLETED
   BOOKED → CANCELLED
   ```

4. **Community Management**:
    - Only admins can create new communities
    - Community managers are assigned during community creation

## Error Handling
The API returns appropriate HTTP status codes with error details:



