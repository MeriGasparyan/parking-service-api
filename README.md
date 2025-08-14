# Parking Service API Documentation

## Table of Contents

* [Overview](#overview)
* [Authentication](#authentication)
* [API Endpoints](#api-endpoints)

    * [Authentication Controller](#authentication-controller)
    * [Community Controller](#community-controller)
    * [Spot Controller](#spot-controller)
    * [Parking Controller](#parking-controller)
    * [User Controller](#user-controller)
* [Data Models](#data-models)

    * [Booking](#booking)
    * [Spot](#spot)
    * [Community](#community)
    * [User](#user)
* [Permissions](#permissions)
* [Business Rules](#business-rules)
* [Validations](#validations)
* [Error Handling](#error-handling)

---

## Overview

The Parking Service API is a RESTful service for managing parking spots, bookings, communities, and users. It supports role-based access control with JWT authentication and permissions enforcement. Users, admins, and community managers can perform operations based on their assigned roles.

---

## Authentication

* **Method:** JWT (JSON Web Token)
* Include the token in the `Authorization` header for all authenticated requests:
  `Authorization: Bearer <token>`
* **Login Endpoint:** `/api/auth/login`
  Returns a JWT token on successful login. No prior authentication is required.

---

## API Endpoints

### Authentication Controller

#### Login

`POST /api/auth/login`
Authenticate a user and return a JWT token.

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "securepassword123"
}
```

**Response:** JWT token

---

### Community Controller

#### Get Community

`GET /api/communities/{id}`
Retrieve community details by ID.

#### Get Community Spots

`GET /api/communities/{id}/spots`
Retrieve all spots in a community.
**Required Permission:** `VIEW_AVAILABLE_SPOT`

#### Create Community

`POST /api/communities`
**Required Permission:** `CREATE_COMMUNITY`

**Request Body:**

```json
{
  "name": "New Community",
  "address": "456 Oak Ave",
  "managerId": 1
}
```

#### Update Community

`PUT /api/communities/{id}`
**Required Permission:** `UPDATE_COMMUNITY`

**Request Body:**

```json
{
  "name": "Updated Community Name",
  "address": "Updated Address",
  "managerId": 2
}
```

#### Delete Community

`DELETE /api/communities/{id}`
**Required Permission:** `DELETE_COMMUNITY`

---

### Spot Controller

#### Create Spot

`POST /api/spots`
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
**Required Permission:** `UPDATE_SPOT`

**Request Body:** (only fields to update)

```json
{
  "code": "B-203",
  "address": "Near Elevator",
  "spotType": "STANDARD"
}
```

#### Delete Spot

`DELETE /api/spots/{id}`
**Required Permission:** `DELETE_SPOT`

---

### Parking Controller

#### Create Booking

`POST /api/parking/book`
Create a booking for the authenticated user in their own community.

**Required Permission:** `CREATE_BOOKING`

**Request Body:**

```json
{
  "spotId": 1,
  "startTime": "2025-08-15T09:00:00Z",
  "endTime": "2025-08-15T11:00:00Z"
}
```

#### Create Guest Booking

`POST /api/parking/guest-book`
Create a booking for a visitor spot in another community.
**Required Permission:** `CREATE_GUEST_BOOKING`
**Max Duration:** Configurable via `application.properties` (default 120 min)

#### Get Available Spots

`GET /api/parking/{communityId}/available-spots?from={datetime}&to={datetime}`
Retrieve available spots in a community for a given time range.
**Required Permission:** `VIEW_AVAILABLE_SPOT`

#### Parking Status Changes

* **Park in Spot:** `POST /api/parking/{bookingId}/park` → `BookingStatus.PARKED`
* **Leave Spot:** `POST /api/parking/{bookingId}/leave` → `BookingStatus.VACATED`
* **Release Spot:** `POST /api/parking/{bookingId}/release` → `BookingStatus.COMPLETED`
* **Cancel Booking:** `POST /api/parking/{bookingId}/cancel` → `BookingStatus.CANCELLED`
* **Change Status:** `POST /api/parking/{bookingId}/status?status={newStatus}` → `BookingStatus` updated

---

### User Controller

#### Create User

`POST /api/users/community/{communityId}`
Default role: `ROLE_RESIDENT`

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securepassword123"
}
```

#### Create Admin

`POST /api/users/create-admin`
Creates an admin account for testing purposes (no community linked).

#### Update User

`PUT /api/users/{id}`
Update user details (self or requires `MANAGE_USERS` permission for others).

#### Delete User

`DELETE /api/users/{id}`

#### Get User

`GET /api/users/{id}`

#### List All Users

`GET /api/users`

#### Get User Bookings

`GET /api/users/bookings`
Requires permission: `VIEW_ALL_BOOKINGS` or `VIEW_OWN_BOOKING`

#### Get Current Bookings

`GET /api/users/current-bookings`
Requires permission: `VIEW_CURRENT_BOOKINGS`

---

## Data Models

### Booking

```json
{
  "id": 1,
  "userId": 1,
  "spotId": 1,
  "startTime": "ISO-8601 datetime",
  "endTime": "ISO-8601 datetime",
  "status": "RESERVED | PARKED | VACATED | COMPLETED | CANCELLED"
}
```

### Spot

```json
{
  "id": 1,
  "code": "B-202",
  "address": "Near Elevator",
  "spotType": "RESIDENT | VISITOR | HANDICAPPED",
  "communityId": 1
}
```

### Community

```json
{
  "id": 1,
  "name": "Community Name",
  "address": "Address",
  "managerId": 2
}
```

### User

```json
{
  "id": 1,
  "firstname": "John",
  "lastname": "Doe",
  "email": "john.doe@example.com",
  "community": {
      "id": 1,
      "name": "Community Name"
  },
  "role": "ADMIN | COMMUNITY_MANAGER | RESIDENT | VISITOR"
}
```

---

## Permissions

| Permission              | Description                       |
| ----------------------- | --------------------------------- |
| VIEW\_AVAILABLE\_SPOT   | View available parking spots      |
| CREATE\_COMMUNITY       | Create communities                |
| UPDATE\_COMMUNITY       | Update communities                |
| DELETE\_COMMUNITY       | Delete communities                |
| CREATE\_BOOKING         | Create bookings in own community  |
| CREATE\_GUEST\_BOOKING  | Create bookings for visitor spots |
| VIEW\_ALL\_BOOKINGS     | View all bookings in system       |
| VIEW\_OWN\_BOOKING      | View only own bookings            |
| VIEW\_CURRENT\_BOOKINGS | View currently active bookings    |
| PARK\_IN\_SPOT          | Mark booking as parked            |
| RELEASE\_SPOT           | Release a parking spot            |
| CANCEL\_BOOKING         | Cancel a booking                  |
| CHANGE\_BOOKING\_STATUS | Change booking status             |
| CREATE\_SPOT            | Create spots                      |
| UPDATE\_SPOT            | Update spots                      |
| DELETE\_SPOT            | Delete spots                      |
| MANAGE\_USERS           | Update or delete other users      |

---

## Business Rules

1. **Booking Restrictions**

    * Regular users can book only within their community.
    * Visitor spots can only be booked via guest bookings.
    * Guest bookings have maximum duration (default: 120 minutes).
2. **Spot Management**

    * Only community managers can manage spots in their community.
    * Visitor spots must be of type `VISITOR`.
3. **Booking Status Flow**

   ```
   RESERVED → PARKED → VACATED → COMPLETED
   RESERVED → CANCELLED
   ```
4. **Community Management**

    * Only admins can create communities.
    * Managers can be assigned during creation.

---

## Validations

* All `Create` DTOs use `@NotNull`/`@NotBlank` and `@Email` where applicable.
* `CreateBookingDTO` has cross-field validation: `endTime` must be after `startTime`.
* Passwords must be at least 4 characters long.
* Spot types must be valid enums: `RESIDENT | VISITOR | HANDICAPPED`.

---

## Error Handling

* `400 Bad Request` → Validation errors or business rule violations
* `401 Unauthorized` → JWT token missing or invalid
* `403 Forbidden` → Permission denied
* `404 Not Found` → Resource does not exist
* `409 Conflict` → Spot already booked or other conflicts
* `500 Internal Server Error` → Unexpected errors
