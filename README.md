<a id="top"></a>

<div align="center">

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=50&pause=1000&color=2563EB&center=true&vCenter=true&width=700&height=100&lines=Farewell" alt="Farewell"/>

### Modern MTG Deck Builder & Collection Manager

Built with **Spring Boot**, **React**, **TypeScript**, **PostgreSQL**, and **Docker**

</div>

---

## Navigation

* [Overview](#overview)
* [Features](#features)
* [Screenshots](#screenshots)
* [Installation](#installation)
* [Verification](#verification)
* [Contributing](#contributing)
* [Credits](#credits)
* [Star History](#star-history)

---

<a id="overview"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=OVERVIEW" width="450"/>

---

Farewell is an open-source **Magic: The Gathering deckbuilding application** designed to help players build, manage, and analyze their decks and collections.

Inspired by platforms such as **Moxfield** and **Archidekt**, Farewell aims to provide a modern, feature-rich deckbuilding experience while integrating community-driven resources like **Commander Spellbook**.

What began as a passion project to solve my own deckbuilding challenges has evolved into a platform focused on:

* Building and managing decks
* Tracking personal collections
* Discovering card combos and synergies
* Analyzing deck statistics
* Streamlining the brewing process

Whether you're building your next Commander deck, refining a competitive list, or exploring hidden interactions, Farewell provides the tools and information you need in one place.

<div align="center">
  <img src="assets/demo.gif" width="95%" alt="Project Demo"/>
</div>

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="features"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=FEATURES" width="450"/>

---

### Deck Building

* Create and manage Commander decks
* Organize cards using custom categories
* Real-time deck validation and statistics
* Fast card search powered by Scryfall

### Collection Management

* Manage your personal MTG collection
* Track card ownership and quantities
* Monitor collection value
* Search and filter cards instantly

### Combo Discovery

* Integration with Commander Spellbook
* Automatically discover combos in decks
* Explore synergistic card interactions
* Find combo pieces already present in your collection

### Analytics

* Mana curve visualization
* Color distribution analysis
* Card type breakdowns
* Collection valuation insights
* Deck composition statistics

### Technology Stack

* Spring Boot
* React
* TypeScript
* PostgreSQL
* Docker
* Scryfall API
* Commander Spellbook API

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="screenshots"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=SCREENSHOTS" width="450"/>

---

<div align="center">

|                                      Library Browser                                     |                                     Combo Discovery                                     |
| :--------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------: |
| <img src="src/main/resources/static/img/browser.png" width="500" alt="Library Browser"/> | <img src="src/main/resources/static/img/combos.png" width="500" alt="Combo Discovery"/> |

</div>

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="installation"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=INSTALLATION" width="450"/>

---

### Clone the Repository

```bash
git clone https://github.com/ToroDDT/Java-MTG-Deckbuilder.git

cd Java-MTG-Deckbuilder
```

### Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE mtg_deckbuilder;
```

Update your application configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mtg_deckbuilder
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run the Backend

```bash
./mvnw spring-boot:run
```

### Run the Frontend

```bash
cd frontend

npm install

npm run dev
```

### Run with Docker

```bash
docker-compose up --build
```

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="verification"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=VERIFICATION" width="450"/>

---

Run the test suite:

```bash
./mvnw test
```

For frontend tests:

```bash
npm test
```

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="contributing"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=CONTRIBUTING" width="450"/>

---

Contributions are welcome!

If you would like to improve Farewell:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

Please open an issue first if you plan on making significant changes.

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="star-history"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=STAR+HISTORY" width="450"/>

---

[![Stargazers over time](https://starchart.cc/ToroDDT/Java-MTG-Deckbuilder.svg?background=%231f2226\&axis=%23ebbcba\&line=%2360A5FA)](https://starchart.cc/ToroDDT/Java-MTG-Deckbuilder)

<div align="right">
  <a href="#top"><kbd>⬆ Back to Top</kbd></a>
</div>

---

<a id="credits"></a>

<img src="https://readme-typing-svg.herokuapp.com?font=Lexend+Giga&size=25&pause=1000&color=60A5FA&vCenter=true&width=435&height=25&lines=THANK+YOU!" width="450"/>

---

Special thanks to:

* The Commander Spellbook team
* The Scryfall team
* Contributors and testers
* The Magic: The Gathering community

For detailed acknowledgements, see:

```text
CREDITS.md
```

---

<div align="center">

Made with ❤️ for the Magic: The Gathering community.

<sub>Last Updated: June 2026</sub>

</div>

---

This project follows the all-contributors specification. Contributions of any kind are welcome.
