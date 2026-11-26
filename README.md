# BookNest - A Book Sharing App for Book Lovers

<p align="center">
  <img src="screenshots/BookNest.png" alt="BookNest Logo" width="150"/>
</p>

## Objective

The objective of BookNest is to give book lovers a simple place to share the books they are reading and to see what other members are reading. The app works like a small online book club. Users post a book cover with a short caption, and everyone can see all posts in one feed.

## Introduction

BookNest is a mobile app for people who love to read. Each post has a book cover, the book name, the author name and a short caption. All posts appear in one feed, and the newest post is always at the top. A user can search the feed by book name, author or username.

The app is built with **Kotlin** in **Android Studio**. It uses **Firebase Authentication** for login, **Firebase Realtime Database** for data and **Cloudinary** to store the images.

## Features

### Account Management
- Create an account with username, email, password and confirm password.
- Email and username must be unique.
- Strong password rule: at least 6 characters and 2 special characters.
- Log in with email and password. A logged-in user goes straight to the feed next time.
- Reset password. The app checks the old password first.
- Add, drag/zoom and remove a profile picture. If removed, the first letter of the name shows.

### Feed and Search
- Posts are shown as cards in a RecyclerView.
- Each card shows username, date and time (`dd/MM/yyyy hh:mm:ss`), book name, author, photo and caption.
- Newest posts come first.
- Live search by book name, author or username (not case sensitive). Clearing the search box shows all posts again.

### Post Management
- Create a post with one book cover image (with preview), book name, author and caption.
- Caption limit is 100 characters with a live counter (for example `7 / 100`).
- **Your Posts** page shows only the current user's posts.
- Change the image, edit the caption or delete a post. Delete asks for confirmation and removes both the image and the database data.

### Navigation Drawer
- Shows profile picture, username and email.
- Options: Create Post, Your Posts, Reset Password and Logout.

## Workflow

1. Create an account or log in.
2. Browse the feed and search for books, authors or members.
3. Open the drawer and choose **Create Post** to share a book.
4. Open **Your Posts** to change the image, edit the caption or delete a post.
5. Use **Reset Password** to change the password, or **Logout** to sign out.

## Methodology

### Tools and Technologies

| Tool | Use |
|------|-----|
| Kotlin, Android Studio | App development |
| Firebase Authentication | Create account, login, change password |
| Firebase Realtime Database | Stores user and post information |
| Cloudinary | Stores uploaded book cover images |
| Glide | Loads images into the feed |

### Firebase Structure

| Node | What it stores |
|------|----------------|
| `users/{uid}` | username, email, photoUrl, photoPublicId |
| `usernames/{name}` | UID of the owner (keeps each username unique) |
| `posts/{postId}` | uid, username, bookName, author, caption, imageUrl, publicId, timestamp |

The app does not use Firebase Storage. Cloudinary returns an image link and a public id, and the database saves both. The public id is used later to replace or delete the image.

## Screenshots

 | Login | Create Account |
|:---:|:---:|
 | <img width="209" height="261" alt="login page" src="https://github.com/user-attachments/assets/c4849961-65f4-4247-ba1a-9f3f3014efc8" /> | <img width="213" height="332" alt="create account page" src="https://github.com/user-attachments/assets/58ab6787-c2cf-4809-8851-afe0e0d58c0f" /> |

| Feed (with search) | Navigation Drawer | Reset Password |
|:---:|:---:|:---:|
| <img width="216" height="361" alt="feed" src="https://github.com/user-attachments/assets/2971dc10-7aee-4115-bfbb-eaaa2b043c84" /> | <img width="202" height="317" alt="drawer" src="https://github.com/user-attachments/assets/13161454-f821-4e38-9e9d-3fddaa0422ea" /> | <img width="209" height="313" alt="reset password" src="https://github.com/user-attachments/assets/9e571a29-fcea-4af5-991e-5356f03a6800" /> |

| Create Post | Your Posts | |
|:---:|:---:|:---:|
| <img width="138" height="290" alt="create post" src="https://github.com/user-attachments/assets/58949888-53d3-4c0a-9fc0-ccdf262ea416" /> | <img width="149" height="255" alt="your post" src="https://github.com/user-attachments/assets/2290e3ec-f51d-4592-a6a8-5f72d6c8d49b" /> | |


## Challenges and Solutions

| Problem | Solution |
|---------|----------|
| `boxCornerRadius` not found | Attribute does not exist; used the four corner attributes instead |
| Cloudinary code was red | Added the library in Gradle and synced the project |
| Drawer menu in the wrong folder | Moved the file from `layout` to `menu` |
| Drawer icons did not show | Made vector icons and removed the icon tint |
| Book covers were cut in the feed | Changed the image view to show the full cover |
| Unique username | Added a `usernames` node to check uniqueness |
| Delete image from Cloudinary | Saved the public id of each image and used it for deletion |

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/kamrunnaharprity2005-stack/BookNest.git
   ```
2. Open the project in Android Studio and let Gradle sync.
3. Create a Firebase project, enable **Email/Password** sign-in and **Realtime Database**, then add your own `google-services.json` to the `app/` folder.
4. Create a Cloudinary account and add your own cloud name and upload settings in the project.
5. Build and run the app on an emulator or a physical device.

> **Note:** Do not upload secret keys (such as the Cloudinary API secret) to a public repository.

## Requirements

- Android Studio
- Android SDK
- A Firebase project
- A Cloudinary account

## Future Plans

- Likes and comments on posts
- Book ratings and short reviews
- User profile page showing all posts of one member
- Safer image delete using a server, so the secret key stays safe

## Contributions

Contributions are welcome. If you find an issue or have a suggestion, feel free to open an issue or submit a pull request.

## Author

**Kamrun Nahar Prity**
Roll: 2320461 Batch: 232
Department of Computer Science & Engineering
Course: Mobile Apps Development (CSE 0613-4145)

Submitted to: Israt Tasnim Esha, Lecturer, Department of CSE

## References

- [Firebase Docs](https://firebase.google.com/docs)
- [Cloudinary Docs](https://cloudinary.com/documentation)
- [Kotlin for Android](https://developer.android.com/kotlin)
- [Glide](https://github.com/bumptech/glide)
