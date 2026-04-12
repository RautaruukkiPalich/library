# Media Service API

Media service for uploading, processing, and managing media files (images, videos).

## Authentication

All endpoints (except `@PublicEndpoint`) require Bearer token authentication:

```text
"Authorization": "Bearer <your_jwt_token>"
```

**Public endpoints:**

- `GET /api/media/{mediaUuid}`
- `GET /api/media/{mediaUuid}/download`

**Protected endpoints (require authentication):**

- `POST /api/media/upload`
- `GET /api/media/tasks`
- `GET /api/media/tasks/{taskUuid}`
- `DELETE /api/media/{mediaUuid}`
- `POST /api/media/{mediaUuid}/task`

## Supported Media Types

| Type  | Extensions                | Content Types                                |
|-------|---------------------------|----------------------------------------------|
| IMAGE | jpeg, jpg, png, gif, webp | image/jpeg, image/png, image/gif, image/webp |

## Size Definitions

| Size      | Max Width | Max Height | Keep Aspect Ratio | Crop to square |
|-----------|-----------|------------|-------------------|----------------|
| ORIGINAL  | -         | -          | -                 | -              |
| LARGE     | 1920      | 1080       | true              | false          |
| MEDIUM    | 800       | 600        | true              | false          |
| SMALL     | 400       | 300        | true              | false          |
| THUMBNAIL | 150       | 150        | false             | true           |
| ICON      | 50        | 50         | false             | true           |

## API METHODS

### POST /api/media/upload

**Request:**

- Content-Type: `multipart/form-data`

| Parameter | Type          | Required | Description          |
|-----------|---------------|----------|----------------------|
| file      | MultipartFile | true     | Media file to upload |

**Response:** `202 ACCEPTED`

```json
{
  "media_uuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Errors**

| Status | Description            |
|:-------|:-----------------------|
| 401    | Unauthorized           |
| 403    | Forbidden              |
| 415    | Unsupported media type |

----

### GET /api/media/tasks

**Response:** `200 OK`

```json
[
  {
    "task_uuid": "660e8400-e29b-41d4-a716-446655440001",
    "media_uuid": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    "status_check_url": "/api/media/tasks/660e8400-e29b-41d4-a716-446655440001"
  }
]
```

**Errors**

| Status | Description  |
|:-------|:-------------|
| 401    | Unauthorized |
| 403    | Forbidden    |

----

### GET /api/media/tasks/{taskUuid}

**Request**

- Path parameters

| Parameter | Type | Required | Description     |
|-----------|------|----------|-----------------|
| taskUuid  | UUID | true     | Task identifier |

**Response:** `200 OK`

```json
{
  "task_uuid": "660e8400-e29b-41d4-a716-446655440001",
  "media_uuid": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "status_check_url": "/api/media/tasks/660e8400-e29b-41d4-a716-446655440001"
}
```

**Errors**

| Status | Description  |
|:-------|:-------------|
| 401    | Unauthorized |
| 403    | Forbidden    |
| 404    | Not found    |

----

### GET /api/media/{mediaUuid}

**Request**

- Path parameters

| Parameter | Type | Required | Description      |
|-----------|------|----------|------------------|
| mediaUuid | UUID | true     | Media identifier |

**Response:** `200 OK`

```json
{
  "media_uuid": "550e8400-e29b-41d4-a716-446655440000",
  "user_id": 12345,
  "original_filename": "photo.jpg",
  "sizes": [
    {
      "content_type": "image/jpeg",
      "media_size": "ORIGINAL",
      "file_size": 2048576,
      "download_url": "/api/media/550e8400-e29b-41d4-a716-446655440000/download?size=ORIGINAL"
    },
    {
      "content_type": "image/webp",
      "media_size": "SMALL",
      "file_size": 102400,
      "download_url": "/api/media/550e8400-e29b-41d4-a716-446655440000/download?size=SMALL"
    }
  ]
}
```

**Errors**

| Status | Description  |
|:-------|:-------------|
| 401    | Unauthorized |
| 403    | Forbidden    |
| 404    | Not found    |

----

### DELETE /api/media/{mediaUuid}

**Request**

- Path parameters

| Parameter | Type | Required | Description      |
|-----------|------|----------|------------------|
| mediaUuid | UUID | true     | Media identifier |

**Response:** `204 NO CONTENT`

**Errors**

| Status | Description  |
|:-------|:-------------|
| 401    | Unauthorized |
| 403    | Forbidden    |
| 404    | Not found    |

----

### POST /api/media/{mediaUuid}/task

**Request:**

- Path parameters

| Parameter | Type | Required | Description      |
|-----------|------|----------|------------------|
| mediaUuid | UUID | true     | Media identifier |

- Request Body: None

**Response:** `202 ACCEPTED`

```json
{
  "task_uuid": "660e8400-e29b-41d4-a716-446655440001",
  "media_uuid": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "status_check_url": "/api/media/tasks/660e8400-e29b-41d4-a716-446655440001"
}
```

**Errors**

| Status | Description            |
|:-------|:-----------------------|
| 401    | Unauthorized           |
| 403    | Forbidden              |
| 415    | Unsupported media type |

----

### GET /api/media/{mediaUuid}/download

**Request**

- Path parameters

| Parameter | Type | Required | Description      |
|-----------|------|----------|------------------|
| mediaUuid | UUID | true     | Media identifier |

- Query parameters

| Parameter | 	Type    | 	Required | Default  | 	Description                                                |
|-----------|----------|-----------|----------|-------------------------------------------------------------|
| size      | 	String  | 	false    | ORIGINAL | 	File size: ORIGINAL, SMALL, MEDIUM, LARGE, ICON, THUMBNAIL |
| inline    | 	boolean | 	false    | false    | 	true for inline display, false for attachment              |

**Response:** `200 OK`

```text
Content-Type: image/jpeg
Content-Disposition: attachment; filename="photo.jpg"
Content-Length: 2048576

(binary data)
```

**Errors**

| Status | Description  |
|:-------|:-------------|
| 401    | Unauthorized |
| 403    | Forbidden    |
| 404    | Not found    |

## Workflow Example

### Upload and Process Image

1. Upload file

```bash
curl -X POST /api/media/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@photo.jpg"
```

```json
{
  "media_uuid": "550e8400-..."
}
```

2. Create processing task

```bash
curl -X POST /api/media/550e8400-.../task \
    -H "Authorization: Bearer <token>"
```

```json
{"task_uuid": "660e8400-...", "status": "PENDING"}
```

3. Check task status

```bash
curl -X GET /api/media/tasks/660e8400-... \
-H "Authorization: Bearer <token>"
```

```json
{
  "status": "COMPLETED"
}
```
