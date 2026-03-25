# Motivations on why to start the project 
I started working on this project after experiencing limitations with websites like Moxfield. While the platform is effective for building decks and providing useful information, it falls short in several key areas.
One major issue I encountered with Moxfield was its limited support for managing personal card collections. I wanted more control over tracking the cards I own, including where they are used and how they are distributed across my decks.
I also wanted better analytics for my collection, such as insights into mana distribution, the cost of individual cards, and the total value of my collection. These features would help me better understand and optimize both my decks and my overall inventory.
Additionally, I was looking for a more simplified user interface for adding personal cards to specific decks. The process on existing platforms often feels more complicated than necessary.

  Another goal was to evaluate how my decks perform against others. I wanted a way to manage my friends’ decks, compare builds, and draw inspiration from their strategies.
Finally, I wanted to improve the card search experience. Existing tools, such as the Scryfall API, can be complex and difficult to navigate due to their many filters and submenus. I aim to integrate machine learning to simplify this process, making card searches more intuitive and user-friendly.

# Vision and Scope Document
https://docs.google.com/document/d/1ML6qYriH7DKFijyOO23LfxHKEfjlWr9qMn-fBJCjRMk/edit?tab=t.0

## Summary of Vision and Scope Document
  The main business problem this application addresses is reducing friction in the deck-building process by providing a single platform for finding, creating, cataloging, and sending decks to local card shops.
This application aims to make it simple and convenient for users to send completed decks to local card shops with the click of a button. While many deck-building websites offer strong integration with online card retailers, they often lack an easy way to connect with local card shops in the same seamless manner.
Another issue this application tackles is the management of personal card collections. Many users need the same cards across multiple decks, yet they often lack an effective way to track what they already own. As a result, users may end up requesting or purchasing cards they already have.
This application streamlines the process by helping users accurately determine which cards they need to complete their decks, ensuring that only necessary cards are sent to their local card shop.

# Lessons Learned while building this tool
## Lesson 1 Introducing Frameworks Too Early.
  One of the biggest mistakes I made in this project was introducing frameworks too early. When I began, I chose React because I believed it was the best option for dynamically updating information and partially rendering the UI while maintaining application state.
At the time, I assumed React would simplify state management and help me build a more responsive interface. In reality, it introduced far more complexity than it solved. State management quickly became a challenge, as I now had to manage state in both the frontend and the backend.

  Instead of using server-side rendering (SSR), where the server is responsible for maintaining and hydrating state, I split responsibility across two layers. This made even basic functionality—such as authentication—significantly more complex. Previously, authentication could be handled entirely on the backend. 
With a separate frontend, I had to consider solutions like Clerk or token-based authentication flows. If the backend needed user information, it had to rely on tokens passed from the frontend, adding another layer of coordination.
This complexity was further compounded by browser security constraints. Because the frontend and backend were hosted on separate domains, standard cookie-based authentication no longer worked reliably due to cross-site cookie restrictions. 
One workaround was to move both services under subdomains, but this introduced additional infrastructure challenges—especially since both applications were running in Docker containers.
To make this setup work, I had to introduce a reverse proxy such as Traefik or NGINX. This added yet another layer of configuration and made the development environment significantly harder to manage. Debugging issues in this setup became time-consuming and frustrating.
An alternative approach was to serve the frontend as static files from the backend. However, this came with its own trade-offs. It made development slower and less efficient, as the React application had to be rebuilt and served statically for changes to take effect. 
Error messages were also less helpful, making debugging more difficult.

  This experience reinforced a broader lesson: introducing frameworks before they are truly necessary can create unnecessary complexity. I encountered a similar issue on the backend when I chose Java Persistence API over simpler approaches like JDBC. While JPA can improve developer productivity, it also abstracts away important implementation details, which can make debugging more difficult.
For example, I ran into a bug caused by bidirectional relationships between entities. Because these entities referenced each other, the JSON serializer attempted to traverse the object graph indefinitely, resulting in infinite recursion and deeply nested, unusable responses.
Although the root cause was related to how Hibernate manages entity relationships, the error messages did not clearly point to the issue. This made it harder to diagnose and reinforced the importance of understanding how abstractions behave under the hood.
I realized with all these situations, that introducing abstractions to soon had their tradeoffs and introducing abstractions to soon without knowing the fundamentals or internal workings was a recipe to headaches. 
