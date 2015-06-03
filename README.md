# TwitterPlus
This app is an android app that allows a Twitter user to login, view home and mentions timelines, view user profiles with user timelines, compose and post a new tweet, as well as retweet/reply/favorite/unfavorite tweets. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public). What is new in my TwitterRedux compared to my [Twitter](https://github.com/srivats666/TwitterClient)? The use of fragments, tabs, and progress bar for network requests.


Time spent: 20 hours spent in total in addition to 40 hours spent on this [Twitter App](https://github.com/srivats666/TwitterClient)

## User Stories

The following **required** functionality is completed:

* [x] The app includes **all required user stories** from Week 3 Twitter Client
* [x] User can **switch between Timeline and Mention views using tabs**
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to **view their own profile**
  * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [x] Profile view includes that user's timeline
* [x] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [x] User can view following / followers list through the profile
* [x] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [x] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [x] User can **"reply" to any tweet on their home timeline**
  * [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [x] User can take favorite (and unfavorite) or retweet actions on a tweet
* [x] Improve the user interface and theme the app to feel twitter branded
* [ ] User can **search for tweets matching a particular query** and see results

The following **bonus** features are implemented:

* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [x] Use of ViewHolder pattern and Parcelable to improve app performance
* [x] Organizing the code through packages and externalizing all string resources into strings.xml
* [x] User can reply, retweet, favourite on the detail page
* [x] User can see **retweet count** and **favorites count** for each tweet/
* [x] User can see an indicator text to distinguish if the tweet is a **retweet** or a **reply** for each tweet.
* [x] User can see embedded image and video media within the tweet detail view
* [x] Vertically **scrollable** detail view. 
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page

## Video Walkthrough 

<img src='https://github.com/srivats666/TwitterPlus/blob/master/Twitter.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Please note that this app built on top of my previous [Twitter App](https://github.com/srivats666/TwitterClient)

Cleaned up the code in TimelineActivity and the Adapter and moved them to the fragment.

## Open-source libraries used

- [RestClient Template](https://github.com/codepath/android-rest-client-template) - Android OAuth REST Client 
- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [2015] [Srivats Jayaram]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
