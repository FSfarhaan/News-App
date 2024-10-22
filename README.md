---

# 📰 News App

## 📖 Description

The **News App** is an Android application built with Java and XML that allows users to stay updated with the latest news across various categories. Users can browse news by category, search for news based on keywords, and save articles for later reading. The app fetches news data from the **gnews.io** API, offering a seamless and intuitive interface for keeping up with current events.

## ✨ Features

- 🌐 **Category-Wise News**: Fetch news based on categories like Entertainment, Sports, Politics, Technology, and more.
- 🔍 **Search News**: Search for news articles based on keywords.
- ⏳ **Watch Later**: Mark articles to read or watch later.
- 📜 **View Full Articles**: Click on any article to view full details with an option to open the original source.
- 📤 **Share News**: Share news articles with your friends easily.
- 🌈 **UI Enhancements**: Added Shimmers and a bottom sheet for a better user interface.
- 🗂️ **Filters**: Filter news based on language and country for customized news results.
- 🔔 **Notification Settings**: Customize notifications using Alarm Manager. Notifications are set to trigger daily at a user-chosen hour, with options for one, two, or four notifications per day.

## 🚀 Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/FSfarhaan/News-App.git
    ```

2. **Open the project in Android Studio**:
    - Open Android Studio
    - Click on `File -> Open`
    - Select the cloned repository folder

3. **Set up API Key**:
    - Sign up on [gnews.io](https://gnews.io) and get your API key.
    - Change the API key in the line `String API_KEY = "YOUR_API_KEY";` in the `HomeFragment` and `SearchFragment` file of the project.

4. **Build and Run the app**:
    - Connect your Android device or start an emulator.
    - Click on the `Run` button in Android Studio.

## 🛠️ Usage

1. **Browsing News by Category**:
    - Open the app and choose a category from the top navigation (e.g., Entertainment, Sports, etc.).
    - Browse through the list of articles in your selected category.

2. **Searching for News**:
    - Use the search bar to enter keywords and fetch relevant articles.

3. **Watch Later**:
    - Tap the save icon on any article to save it to your 'Watch Later' list.

4. **View Full Article**:
    - Click on any news item to read more details, or follow the link to the original article for the full content.

5. **Sharing News**:
    - Share any news article with your friends directly from the app.

## 🧩 Code Overview

### API Integration

- **gnews.io API**: The app uses the gnews.io API to fetch news based on categories and search terms.
- **Retrofit**: A type-safe HTTP client is used for making API calls.

## 📱 Screenshots

<!-- Add screenshots of your app here. Example: -->
![Image 1](screenshots/image1.jpg)
![Image 2](screenshots/image2.jpg)
![Image 3](screenshots/image3.jpg)

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the app or add new features, feel free to create an issue or submit a pull request.

## 📬 Contact

If you have any questions or suggestions, feel free to contact me at [farhaan8d@gmail.com](mailto:farhaan8d@gmail.com).  
Connect with me on [LinkedIn](https://www.linkedin.com/in/farhaan-shaikh-422301252/).

--- 
