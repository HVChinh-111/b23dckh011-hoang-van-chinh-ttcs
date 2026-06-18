# API Endpoints — Personal Blog Backend

Base URL: `http://localhost:8080`

Tất cả response body dùng format thống nhất:

- **Thành công (có data):** `{ "data": <object | array> }`
- **Thành công (không có data):** HTTP `204 No Content`
- **Lỗi:** `{ "code": "<ERROR_CODE>", "message": "<mô tả>" }`

Timestamps trả về theo định dạng ISO-8601: `"2024-06-17T08:30:00"`

---

## Mục lục

1. [Auth](#1-auth)
2. [Profile](#2-profile)
3. [Media](#3-media)
4. [Topic](#4-topic)
5. [Series](#5-series)
6. [Post — Public](#6-post--public)
7. [Post — Admin](#7-post--admin)

---

## 1. Auth

### POST `/api/auth/login`

Đăng nhập, nhận cặp Access Token + Refresh Token.

- **Auth:** Không yêu cầu

**Request Body** (`application/json`):

```json
{
  "email": "admin@blog.local",
  "password": "Admin@12345"
}
```

| Field      | Type   | Bắt buộc | Mô tả                        |
|------------|--------|----------|------------------------------|
| `email`    | string | Có       | Phải đúng định dạng email    |
| `password` | string | Có       | Mật khẩu của admin           |

**Response 200:**

```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "a1b2c3d4e5f6...",
    "tokenType": "Bearer",
    "accessTokenExpiresAt": "2024-06-17T09:00:00",
    "refreshTokenExpiresAt": "2024-07-17T08:30:00"
  }
}
```

| Field                   | Type   | Mô tả                                  |
|-------------------------|--------|----------------------------------------|
| `accessToken`           | string | JWT dùng để gọi các admin endpoint     |
| `refreshToken`          | string | Token dùng để lấy cặp token mới        |
| `tokenType`             | string | Luôn là `"Bearer"`                     |
| `accessTokenExpiresAt`  | string | Thời điểm hết hạn access token (30 phút) |
| `refreshTokenExpiresAt` | string | Thời điểm hết hạn refresh token (30 ngày) |

**Errors:**

| HTTP | code                  | Khi nào                          |
|------|-----------------------|----------------------------------|
| 400  | `VALIDATION_ERROR`    | email/password để trống hoặc sai format |
| 401  | `INVALID_CREDENTIALS` | Email hoặc mật khẩu không đúng  |

---

### POST `/api/auth/refresh`

Đổi Refresh Token lấy cặp token mới (token rotation).

- **Auth:** Không yêu cầu

**Request Body** (`application/json`):

```json
{
  "refreshToken": "a1b2c3d4e5f6..."
}
```

| Field          | Type   | Bắt buộc | Mô tả                       |
|----------------|--------|----------|-----------------------------|
| `refreshToken` | string | Có       | Refresh token hiện tại      |

**Response 200:** Cấu trúc giống `/api/auth/login` — trả về cặp token mới.

**Errors:**

| HTTP | code               | Khi nào                                         |
|------|--------------------|-------------------------------------------------|
| 400  | `VALIDATION_ERROR` | `refreshToken` để trống                         |
| 401  | `UNAUTHORIZED`     | Token không tồn tại, đã bị thu hồi, hoặc hết hạn |

---

### POST `/api/auth/logout`

Đăng xuất, thu hồi Refresh Token.

- **Auth:** Bắt buộc (`Authorization: Bearer <accessToken>`)

**Request Body** (`application/json`):

```json
{
  "refreshToken": "a1b2c3d4e5f6..."
}
```

| Field          | Type   | Bắt buộc | Mô tả                       |
|----------------|--------|----------|-----------------------------|
| `refreshToken` | string | Có       | Refresh token cần thu hồi   |

**Response 204:** No Content

**Errors:**

| HTTP | code               | Khi nào                       |
|------|--------------------|-------------------------------|
| 400  | `VALIDATION_ERROR` | `refreshToken` để trống       |
| 401  | `UNAUTHORIZED`     | Không có hoặc sai access token |

---

## 2. Profile

### GET `/api/profile`

Lấy thông tin tác giả blog.

- **Auth:** Không yêu cầu

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "fullName": "Hoàng Văn Chính",
    "shortBio": "Backend Developer, yêu thích Java và Spring Boot.",
    "githubUrl": "https://github.com/hvchinh",
    "linkedInUrl": "https://linkedin.com/in/hvchinh",
    "facebookUrl": "https://facebook.com/hvchinh",
    "contactEmail": "chinh@example.com",
    "avatar": {
      "id": "uuid-string",
      "publicPath": "/uploads/avatars/abc123.jpg",
      "mimeType": "image/jpeg"
    }
  }
}
```

> `avatar` là `null` khi chưa upload ảnh đại diện.

---

### PUT `/api/profile`

Cập nhật thông tin tác giả, tuỳ chọn thay ảnh đại diện.

- **Auth:** Bắt buộc
- **Content-Type:** `multipart/form-data`

**Form Parts:**

| Part   | Type                      | Bắt buộc | Mô tả                                              |
|--------|---------------------------|----------|----------------------------------------------------|
| `data` | JSON (`application/json`) | Có       | Thông tin profile (xem bên dưới)                   |
| `avatar` | File (image)            | Không    | Ảnh đại diện mới (jpg/jpeg/png/webp, tối đa 2 MB)  |

**Part `data`** (`application/json`):

```json
{
  "fullName": "Hoàng Văn Chính",
  "shortBio": "Backend Developer.",
  "githubUrl": "https://github.com/hvchinh",
  "linkedInUrl": "https://linkedin.com/in/hvchinh",
  "facebookUrl": "https://facebook.com/hvchinh",
  "contactEmail": "chinh@example.com"
}
```

| Field        | Type   | Bắt buộc | Mô tả                                       |
|--------------|--------|----------|---------------------------------------------|
| `fullName`   | string | Có       | Họ tên                                      |
| `shortBio`   | string | Không    | Giới thiệu ngắn (HTML sẽ bị sanitize)       |
| `githubUrl`  | string | Không    | Phải là URL hợp lệ nếu có giá trị           |
| `linkedInUrl`| string | Không    | Phải là URL hợp lệ nếu có giá trị           |
| `facebookUrl`| string | Không    | Phải là URL hợp lệ nếu có giá trị           |
| `contactEmail`| string| Không    | Phải là email hợp lệ nếu có giá trị         |

**Response 200:** Cấu trúc giống `GET /api/profile`.

**Errors:**

| HTTP | code               | Khi nào                                     |
|------|--------------------|---------------------------------------------|
| 400  | `VALIDATION_ERROR` | `fullName` để trống hoặc URL/email sai format |
| 400  | `FILE_STORAGE_ERROR` | File quá 2 MB hoặc sai định dạng          |
| 401  | `UNAUTHORIZED`     | Không có access token                        |

---

## 3. Media

### POST `/api/media/images`

Upload ảnh nội dung bài viết (dùng trong Markdown editor).

- **Auth:** Bắt buộc
- **Content-Type:** `multipart/form-data`

**Form Part:**

| Part   | Type  | Bắt buộc | Mô tả                                          |
|--------|-------|----------|------------------------------------------------|
| `file` | File  | Có       | Ảnh (jpg/jpeg/png/webp, tối đa 5 MB)           |

**Response 200:**

```json
{
  "data": {
    "url": "/uploads/images/a1b2c3d4.jpg",
    "mimeType": "image/jpeg"
  }
}
```

| Field      | Type   | Mô tả                                         |
|------------|--------|-----------------------------------------------|
| `url`      | string | Đường dẫn công khai để chèn vào Markdown      |
| `mimeType` | string | MIME type của file                             |

**Errors:**

| HTTP | code                 | Khi nào                                    |
|------|----------------------|--------------------------------------------|
| 400  | `FILE_STORAGE_ERROR` | File thiếu, quá 5 MB, hoặc sai định dạng  |
| 401  | `UNAUTHORIZED`       | Không có access token                      |

---

## 4. Topic

### GET `/api/topics`

Lấy danh sách tất cả topic kèm số bài viết đã published.

- **Auth:** Không yêu cầu

**Response 200:**

```json
{
  "data": [
    {
      "id": "uuid-string",
      "name": "Java",
      "slug": "java",
      "description": "Lập trình Java và JVM ecosystem.",
      "publishedPostCount": 12
    }
  ]
}
```

---

### GET `/api/topics/{slug}/posts`

Lấy danh sách bài viết đã published thuộc một topic (có phân trang).

- **Auth:** Không yêu cầu

**Path Params:**

| Param  | Mô tả       |
|--------|-------------|
| `slug` | Slug của topic |

**Query Params:**

| Param  | Mặc định | Mô tả            |
|--------|----------|------------------|
| `page` | `1`      | Số trang (1-based) |
| `size` | `10`     | Số item mỗi trang |

**Response 200:**

```json
{
  "data": {
    "topicInfo": {
      "id": "uuid-string",
      "name": "Java",
      "slug": "java",
      "description": "Lập trình Java và JVM ecosystem."
    },
    "posts": [
      {
        "id": "uuid-string",
        "title": "Spring Boot 4 là gì?",
        "slug": "spring-boot-4-la-gi",
        "summary": "Tổng quan về Spring Boot 4...",
        "publishedAt": "2024-06-17T08:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalItems": 25,
      "totalPages": 3,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

**Errors:**

| HTTP | code              | Khi nào              |
|------|-------------------|----------------------|
| 404  | `TOPIC_NOT_FOUND` | Slug không tồn tại   |

---

### POST `/api/topics`

Tạo topic mới.

- **Auth:** Bắt buộc

**Request Body** (`application/json`):

```json
{
  "name": "Spring Boot",
  "slug": "spring-boot",
  "description": "Framework Java phổ biến nhất."
}
```

| Field         | Type   | Bắt buộc | Mô tả                                            |
|---------------|--------|----------|--------------------------------------------------|
| `name`        | string | Có       | Tên topic (tối đa 255 ký tự, phải duy nhất)      |
| `slug`        | string | Có       | Slug URL (tối đa 255 ký tự, phải duy nhất, chỉ `a-z0-9-`) |
| `description` | string | Không    | Mô tả                                            |

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "name": "Spring Boot",
    "slug": "spring-boot",
    "description": "Framework Java phổ biến nhất."
  }
}
```

**Errors:**

| HTTP | code                | Khi nào                              |
|------|---------------------|--------------------------------------|
| 400  | `VALIDATION_ERROR`  | `name` hoặc `slug` để trống         |
| 400  | `INVALID_SLUG`      | Slug sai định dạng                   |
| 409  | `DUPLICATE_SLUG`    | `name` hoặc `slug` đã tồn tại       |
| 401  | `UNAUTHORIZED`      | Không có access token                |

---

### PUT `/api/topics/{id}`

Cập nhật topic.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả        |
|-------|--------------|
| `id`  | UUID của topic |

**Request Body:** Cấu trúc giống `POST /api/topics`.

**Response 200:** Cấu trúc giống `POST /api/topics`.

**Errors:**

| HTTP | code                | Khi nào                                   |
|------|---------------------|-------------------------------------------|
| 400  | `VALIDATION_ERROR`  | `name` hoặc `slug` để trống              |
| 400  | `INVALID_SLUG`      | Slug sai định dạng                        |
| 404  | `TOPIC_NOT_FOUND`   | `id` không tồn tại                        |
| 409  | `DUPLICATE_SLUG`    | `name` hoặc `slug` đã dùng bởi topic khác |
| 401  | `UNAUTHORIZED`      | Không có access token                     |

---

### DELETE `/api/topics/{id}`

Xóa topic. Các bài viết đang gắn topic này sẽ **không bị xóa**, chỉ gỡ liên kết.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả        |
|-------|--------------|
| `id`  | UUID của topic |

**Response 204:** No Content

**Errors:**

| HTTP | code              | Khi nào             |
|------|-------------------|---------------------|
| 404  | `TOPIC_NOT_FOUND` | `id` không tồn tại  |
| 401  | `UNAUTHORIZED`    | Không có access token |

---

## 5. Series

### GET `/api/series`

Lấy danh sách tất cả series kèm số bài viết.

- **Auth:** Không yêu cầu

**Response 200:**

```json
{
  "data": [
    {
      "id": "uuid-string",
      "name": "Học Spring Boot từ A đến Z",
      "slug": "hoc-spring-boot",
      "description": "Series học Spring Boot toàn diện.",
      "postCount": 8
    }
  ]
}
```

---

### GET `/api/series/{slug}/posts`

Lấy danh sách bài viết đã published trong series, theo thứ tự series (có phân trang).

- **Auth:** Không yêu cầu

**Path Params:**

| Param  | Mô tả        |
|--------|--------------|
| `slug` | Slug của series |

**Query Params:**

| Param  | Mặc định | Mô tả             |
|--------|----------|-------------------|
| `page` | `1`      | Số trang (1-based) |
| `size` | `10`     | Số item mỗi trang  |

**Response 200:**

```json
{
  "data": {
    "seriesInfo": {
      "id": "uuid-string",
      "name": "Học Spring Boot từ A đến Z",
      "slug": "hoc-spring-boot",
      "description": "Series học Spring Boot toàn diện."
    },
    "items": [
      {
        "seriesPostItemId": "uuid-string",
        "postId": "uuid-string",
        "title": "Bài 1: Spring Boot là gì?",
        "slug": "spring-boot-la-gi",
        "summary": "Tổng quan về Spring Boot...",
        "sequenceNumber": 1,
        "current": false
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalItems": 8,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

> `current` luôn là `false` ở endpoint này; `true` chỉ xuất hiện trong `GET /api/posts/{slug}` khi highlight bài hiện tại.

**Errors:**

| HTTP | code               | Khi nào              |
|------|--------------------|----------------------|
| 404  | `SERIES_NOT_FOUND` | Slug không tồn tại   |

---

### GET `/api/series/{id}`

Lấy chi tiết series để chỉnh sửa (admin).

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của series |

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "name": "Học Spring Boot từ A đến Z",
    "slug": "hoc-spring-boot",
    "description": "Series học Spring Boot toàn diện.",
    "items": [
      {
        "seriesPostItemId": "uuid-string",
        "postId": "uuid-string",
        "title": "Bài 1: Spring Boot là gì?",
        "slug": "spring-boot-la-gi",
        "summary": "Tổng quan về Spring Boot...",
        "sequenceNumber": 1,
        "current": false
      }
    ]
  }
}
```

**Errors:**

| HTTP | code               | Khi nào              |
|------|--------------------|----------------------|
| 404  | `SERIES_NOT_FOUND` | `id` không tồn tại   |
| 401  | `UNAUTHORIZED`     | Không có access token |

---

### POST `/api/series`

Tạo series mới.

- **Auth:** Bắt buộc

**Request Body** (`application/json`):

```json
{
  "name": "Học Spring Boot từ A đến Z",
  "slug": "hoc-spring-boot",
  "description": "Series học Spring Boot toàn diện."
}
```

| Field         | Type   | Bắt buộc | Mô tả                                            |
|---------------|--------|----------|--------------------------------------------------|
| `name`        | string | Có       | Tên series (tối đa 255 ký tự, phải duy nhất)     |
| `slug`        | string | Có       | Slug URL (tối đa 255 ký tự, phải duy nhất, chỉ `a-z0-9-`) |
| `description` | string | Không    | Mô tả                                            |

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "name": "Học Spring Boot từ A đến Z",
    "slug": "hoc-spring-boot",
    "description": "Series học Spring Boot toàn diện.",
    "postCount": 0
  }
}
```

**Errors:**

| HTTP | code                | Khi nào                             |
|------|---------------------|-------------------------------------|
| 400  | `VALIDATION_ERROR`  | `name` hoặc `slug` để trống        |
| 400  | `INVALID_SLUG`      | Slug sai định dạng                  |
| 409  | `DUPLICATE_SLUG`    | `name` hoặc `slug` đã tồn tại      |
| 401  | `UNAUTHORIZED`      | Không có access token               |

---

### PUT `/api/series/{id}`

Cập nhật series và sắp xếp lại thứ tự bài viết trong series.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của series |

**Request Body** (`application/json`):

```json
{
  "name": "Học Spring Boot từ A đến Z",
  "slug": "hoc-spring-boot",
  "description": "Mô tả mới.",
  "postOrders": [
    { "seriesPostItemId": "uuid-1", "sequenceNumber": 1 },
    { "seriesPostItemId": "uuid-2", "sequenceNumber": 2 }
  ]
}
```

| Field           | Type   | Bắt buộc | Mô tả                                              |
|-----------------|--------|----------|----------------------------------------------------|
| `name`          | string | Có       | Tên series                                         |
| `slug`          | string | Có       | Slug URL                                           |
| `description`   | string | Không    | Mô tả                                              |
| `postOrders`    | array  | Không    | Danh sách mới sắp xếp thứ tự bài trong series      |
| `postOrders[].seriesPostItemId` | string | Có | ID của series post item |
| `postOrders[].sequenceNumber`   | int    | Có | Số thứ tự mới (phải > 0)              |

**Response 200:** Cấu trúc giống `POST /api/series`.

**Errors:**

| HTTP | code                | Khi nào                                    |
|------|---------------------|--------------------------------------------|
| 400  | `VALIDATION_ERROR`  | `name`/`slug` trống hoặc `sequenceNumber` ≤ 0 |
| 400  | `INVALID_SLUG`      | Slug sai định dạng                         |
| 404  | `SERIES_NOT_FOUND`  | `id` không tồn tại                         |
| 409  | `DUPLICATE_SLUG`    | `name` hoặc `slug` đã dùng bởi series khác |
| 401  | `UNAUTHORIZED`      | Không có access token                      |

---

### DELETE `/api/series/{id}`

Xóa series. Các bài viết trong series sẽ **không bị xóa**, chỉ gỡ khỏi series.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của series |

**Response 204:** No Content

**Errors:**

| HTTP | code               | Khi nào              |
|------|--------------------|----------------------|
| 404  | `SERIES_NOT_FOUND` | `id` không tồn tại   |
| 401  | `UNAUTHORIZED`     | Không có access token |

---

## 6. Post — Public

### GET `/api/posts`

Lấy danh sách bài viết đã published (trang chủ), sắp xếp mới nhất trước.

- **Auth:** Không yêu cầu

**Query Params:**

| Param  | Mặc định | Mô tả             |
|--------|----------|-------------------|
| `page` | `1`      | Số trang (1-based) |
| `size` | `10`     | Số item mỗi trang  |

**Response 200:**

```json
{
  "data": {
    "posts": [
      {
        "id": "uuid-string",
        "title": "Spring Boot 4 là gì?",
        "slug": "spring-boot-4-la-gi",
        "summary": "Tổng quan về Spring Boot 4, những thay đổi lớn...",
        "publishedAt": "2024-06-17T08:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalItems": 42,
      "totalPages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

---

### GET `/api/posts/search`

Tìm kiếm bài viết theo từ khoá trong tiêu đề (chỉ bài đã published).

- **Auth:** Không yêu cầu

**Query Params:**

| Param     | Mặc định | Bắt buộc | Mô tả                      |
|-----------|----------|----------|----------------------------|
| `keyword` | —        | Có       | Từ khoá tìm kiếm           |
| `page`    | `1`      | Không    | Số trang (1-based)         |
| `size`    | `10`     | Không    | Số item mỗi trang          |

**Response 200:**

```json
{
  "data": {
    "keyword": "spring boot",
    "totalResults": 5,
    "posts": [
      {
        "id": "uuid-string",
        "title": "Spring Boot 4 là gì?",
        "slug": "spring-boot-4-la-gi",
        "summary": "Tổng quan về Spring Boot 4...",
        "publishedAt": "2024-06-17T08:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalItems": 5,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

**Errors:**

| HTTP | code               | Khi nào                              |
|------|--------------------|--------------------------------------|
| 400  | `VALIDATION_ERROR` | `keyword` để trống hoặc chỉ ký tự trắng |

---

### GET `/api/posts/{slug}`

Lấy nội dung đầy đủ một bài viết (chỉ bài đã published). Nội dung trả về HTML đã render từ Markdown và sanitize.

- **Auth:** Không yêu cầu

**Path Params:**

| Param  | Mô tả       |
|--------|-------------|
| `slug` | Slug của bài viết |

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "title": "Spring Boot 4 là gì?",
    "slug": "spring-boot-4-la-gi",
    "contentHtml": "<h1 id=\"spring-boot-4-la-gi\">Spring Boot 4 là gì?</h1><p>...</p>",
    "createdAt": "2024-06-10T07:00:00",
    "updatedAt": "2024-06-17T08:00:00",
    "publishedAt": "2024-06-17T08:00:00",
    "topics": [
      { "id": "uuid-string", "name": "Java", "slug": "java", "description": "..." }
    ],
    "tocItems": [
      { "id": "spring-boot-4-la-gi", "title": "Spring Boot 4 là gì?", "level": 1 },
      { "id": "cai-dat", "title": "Cài đặt", "level": 2 }
    ],
    "seriesInfo": {
      "id": "uuid-string",
      "name": "Học Spring Boot",
      "slug": "hoc-spring-boot",
      "description": "..."
    },
    "seriesItems": [
      {
        "seriesPostItemId": "uuid-string",
        "postId": "uuid-string",
        "title": "Bài 1: Spring Boot là gì?",
        "slug": "spring-boot-la-gi",
        "summary": "...",
        "sequenceNumber": 1,
        "current": true
      }
    ]
  }
}
```

| Field         | Type   | Mô tả                                                      |
|---------------|--------|------------------------------------------------------------|
| `contentHtml` | string | Nội dung Markdown đã render sang HTML, sanitize XSS        |
| `tocItems`    | array  | Mục lục tự động từ các heading trong bài                   |
| `seriesInfo`  | object | Thông tin series (null nếu bài không thuộc series nào)     |
| `seriesItems` | array  | Toàn bộ bài trong series; `current: true` ở bài đang đọc  |

**Errors:**

| HTTP | code             | Khi nào                                       |
|------|------------------|-----------------------------------------------|
| 404  | `POST_NOT_FOUND` | Slug không tồn tại hoặc bài ở trạng thái DRAFT |

---

## 7. Post — Admin

> Tất cả endpoint trong nhóm này yêu cầu `Authorization: Bearer <accessToken>`.

---

### GET `/api/admin/posts`

Lấy danh sách tất cả bài viết (cả DRAFT và PUBLISHED), sắp xếp theo `updatedAt` mới nhất trước.

- **Auth:** Bắt buộc

**Query Params:**

| Param  | Mặc định | Mô tả             |
|--------|----------|-------------------|
| `page` | `1`      | Số trang (1-based) |
| `size` | `10`     | Số item mỗi trang  |

**Response 200:**

```json
{
  "data": {
    "posts": [
      {
        "id": "uuid-string",
        "title": "Spring Boot 4 là gì?",
        "slug": "spring-boot-4-la-gi",
        "status": "PUBLISHED",
        "updatedAt": "2024-06-17T08:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalItems": 15,
      "totalPages": 2,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

> `status`: `"DRAFT"` hoặc `"PUBLISHED"`

---

### GET `/api/admin/posts/form-options`

Lấy danh sách topic và series để render selector trong form tạo/sửa bài viết.

- **Auth:** Bắt buộc

**Response 200:**

```json
{
  "data": {
    "topics": [
      { "id": "uuid-string", "name": "Java" },
      { "id": "uuid-string", "name": "Spring Boot" }
    ],
    "seriesList": [
      { "id": "uuid-string", "name": "Học Spring Boot" }
    ]
  }
}
```

---

### GET `/api/admin/posts/{id}`

Lấy dữ liệu bài viết để load vào form chỉnh sửa.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của bài viết |

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "title": "Spring Boot 4 là gì?",
    "slug": "spring-boot-4-la-gi",
    "contentMarkdown": "# Spring Boot 4 là gì?\n\n...",
    "status": "PUBLISHED",
    "selectedTopicIds": ["uuid-topic-1", "uuid-topic-2"],
    "selectedSeriesId": "uuid-series-1"
  }
}
```

> `selectedSeriesId` là `null` nếu bài không thuộc series nào.

**Errors:**

| HTTP | code             | Khi nào              |
|------|------------------|----------------------|
| 404  | `POST_NOT_FOUND` | `id` không tồn tại   |
| 401  | `UNAUTHORIZED`   | Không có access token |

---

### POST `/api/admin/posts`

Tạo bài viết mới.

- **Auth:** Bắt buộc

**Request Body** (`application/json`):

```json
{
  "title": "Spring Boot 4 là gì?",
  "slug": "spring-boot-4-la-gi",
  "contentMarkdown": "# Spring Boot 4 là gì?\n\nNội dung bài viết...",
  "status": "DRAFT",
  "topicIds": ["uuid-topic-1", "uuid-topic-2"],
  "seriesId": "uuid-series-1"
}
```

| Field             | Type   | Bắt buộc | Mô tả                                                |
|-------------------|--------|----------|------------------------------------------------------|
| `title`           | string | Có       | Tiêu đề (tối đa 255 ký tự)                          |
| `slug`            | string | Có       | Slug URL (tối đa 255 ký tự, phải duy nhất, chỉ `a-z0-9-`) |
| `contentMarkdown` | string | Có       | Nội dung bài viết (Markdown)                         |
| `status`          | string | Có       | `"DRAFT"` hoặc `"PUBLISHED"`                         |
| `topicIds`        | array  | Không    | Danh sách UUID của các topic                         |
| `seriesId`        | string | Không    | UUID của series (null nếu không thuộc series nào)    |

> Khi `status = "PUBLISHED"`, `publishedAt` được set tự động.
> Khi có `seriesId`, bài được thêm vào cuối series với `sequenceNumber = max + 1`.

**Response 200:**

```json
{
  "data": {
    "id": "uuid-string",
    "title": "Spring Boot 4 là gì?",
    "slug": "spring-boot-4-la-gi",
    "status": "DRAFT",
    "createdAt": "2024-06-17T08:00:00",
    "updatedAt": "2024-06-17T08:00:00",
    "publishedAt": null
  }
}
```

**Errors:**

| HTTP | code                | Khi nào                                   |
|------|---------------------|-------------------------------------------|
| 400  | `VALIDATION_ERROR`  | `title`, `slug`, `contentMarkdown`, hoặc `status` để trống/null |
| 400  | `INVALID_SLUG`      | Slug sai định dạng (`^[a-z0-9]+(?:-[a-z0-9]+)*$`) |
| 409  | `DUPLICATE_SLUG`    | Slug đã tồn tại                           |
| 404  | `TOPIC_NOT_FOUND`   | Một trong các `topicIds` không tồn tại    |
| 404  | `SERIES_NOT_FOUND`  | `seriesId` không tồn tại                  |
| 401  | `UNAUTHORIZED`      | Không có access token                     |

---

### PUT `/api/admin/posts/{id}`

Cập nhật bài viết.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của bài viết |

**Request Body:** Cấu trúc giống `POST /api/admin/posts`.

**Response 200:** Cấu trúc giống `POST /api/admin/posts`.

> Khi chuyển từ DRAFT → PUBLISHED, `publishedAt` được set lần đầu.  
> Khi chuyển từ PUBLISHED → DRAFT, `publishedAt` được xoá (set null).

**Errors:**

| HTTP | code                | Khi nào                                        |
|------|---------------------|------------------------------------------------|
| 400  | `VALIDATION_ERROR`  | Giá trị bắt buộc để trống                      |
| 400  | `INVALID_SLUG`      | Slug sai định dạng                              |
| 404  | `POST_NOT_FOUND`    | `id` không tồn tại                             |
| 409  | `DUPLICATE_SLUG`    | Slug đã dùng bởi bài viết khác                 |
| 404  | `TOPIC_NOT_FOUND`   | Một trong các `topicIds` không tồn tại         |
| 404  | `SERIES_NOT_FOUND`  | `seriesId` không tồn tại                       |
| 401  | `UNAUTHORIZED`      | Không có access token                           |

---

### DELETE `/api/admin/posts/{id}`

Xóa bài viết. Tự động xóa các ảnh nội dung đã upload cho bài, gỡ khỏi topic và series.

- **Auth:** Bắt buộc

**Path Params:**

| Param | Mô tả       |
|-------|-------------|
| `id`  | UUID của bài viết |

**Response 204:** No Content

**Errors:**

| HTTP | code             | Khi nào              |
|------|------------------|----------------------|
| 404  | `POST_NOT_FOUND` | `id` không tồn tại   |
| 401  | `UNAUTHORIZED`   | Không có access token |

---

## Tổng hợp Error Codes

| Code                  | HTTP | Mô tả                                              |
|-----------------------|------|----------------------------------------------------|
| `VALIDATION_ERROR`    | 400  | Input không hợp lệ (field bắt buộc, sai format)   |
| `INVALID_SLUG`        | 400  | Slug không khớp `^[a-z0-9]+(?:-[a-z0-9]+)*$`      |
| `FILE_STORAGE_ERROR`  | 400  | File không hợp lệ (kích thước, định dạng)          |
| `INVALID_CREDENTIALS` | 401  | Email hoặc mật khẩu không đúng                    |
| `UNAUTHORIZED`        | 401  | Thiếu hoặc sai/hết hạn access token               |
| `FORBIDDEN`           | 403  | Không có quyền truy cập                            |
| `POST_NOT_FOUND`      | 404  | Bài viết không tồn tại                             |
| `TOPIC_NOT_FOUND`     | 404  | Topic không tồn tại                                |
| `SERIES_NOT_FOUND`    | 404  | Series không tồn tại                               |
| `PROFILE_NOT_FOUND`   | 404  | Profile tác giả không tồn tại                      |
| `DUPLICATE_SLUG`      | 409  | Slug hoặc name đã tồn tại                          |

---

## Ghi chú Authentication

Các endpoint yêu cầu admin phải gửi header:

```
Authorization: Bearer <accessToken>
```

Access token có thời hạn **30 phút**. Khi hết hạn, dùng `POST /api/auth/refresh` để lấy cặp token mới (refresh token xoay vòng — token cũ bị thu hồi ngay sau khi refresh).

File tĩnh (ảnh đã upload) được phục vụ trực tiếp tại:

```
GET /uploads/<path>
```

Ví dụ: `/uploads/images/abc123.jpg` — không yêu cầu auth.
