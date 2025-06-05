Below is a deep‑dive tour of the **fivel** codebase, broken into plain‑spoken, chatty chunks. I shoot for ±3 000 words (give or take a typo or two), so settle in and skim the headings if you need landmarks.

---

### 1  What the project is in one breath

Fivel is a lightweight Scala 3 toolkit for *randomized text generation* that doubles as a tiny web service. It chews on plain‑text “tables” plus word lists, rolls virtual dice, plugs the results into templated braces, and spits back sentences, names, plot hooks, artifacts—anything you can encode as text. The JAR boots an embedded Jetty server on `localhost:8888`, so the whole thing feels like a self‑hosted micro‑API rather than a desktop toy.

---

### 2  High‑level architecture

At top level you see:

* **`build.sbt`** – locks Scala 3.1.1, pulls Jetty 9, Jackson, ScalaTest, and H2 (not fully used yet).
* **`Main.scala`** – wires path prefixes (`/`, `/hello`, `/tables/*`, `/words/*`) to servlet classes and starts the server.
* **`servel` package** – thin wrappers around Jetty’s `HttpServlet`, plus a `ServerRunner` helper that watches for Ctrl‑C or Enter.
* **`models` package** – the real meat:

    * `basic` – utilities for deterministic randomness and file loading.
    * `tables` – parser, row/roller logic, dice evaluator.
    * `words` – n‑gram “associator” that invents novel strings.

Resources live in `/src/main/resources/{tables,words}`; everything else is pure code.

---

### 3  Static content and hello world

`StaticContentServlet` streams whatever sits under `/public`. Hit the root URL and Jetty serves `index.html` straight off the classpath. That means you can plant a one‑page React bundle in `public` and have an instant UI without touching routes. Meanwhile `HelloWorldServlet` exists purely as a sanity check: return JSON, see it appear, smile, move on.

---

### 4  Resource loader: one class, two worlds

`models.basic.Resource` is a sneaky MVP. It hides away the annoyance of *“am I running from the filesystem or from inside a fat JAR?”* and provides two things:

1. `toSeq` – load the file’s lines.
2. `paths` – if the “file” is actually a directory, list the children; if we’re in a JAR, walk the entries with a regex filter.

That trick lets the same code run in `sbt run`, `sbt assembly`, or inside Docker without special‑casing anything.

---

### 5  Parsing table text: indentation rules

A table file looks like:

```
= Fantasy Title
  - The {|fantasy subject} of {|fantasy theme}
  - {|fantasy theme}, {|fantasy subject} and {|fantasy theme}
```

* A line starting with `=` opens a new table with that name.
* A line starting with `-` (optionally preceded by spaces) declares a row.
* Leading spaces equal nesting level, so you can build sub‑tables or weighted groups.
* Pipe‑braced `{|foo}` chunks are placeholders for *other* tables or word lists.
* Square‑braced `[2d6]` rolls dice.
* Curlies `{something}` just trigger evaluation.

`models.tables.Parse` is an imperative state machine that walks a `BufferedReader`. It tracks four states (`StartLine`, `StartTable`, `Indented`, `Counted`) and spits out a list of `Table` objects, each with a mutable collection of `Row`s.

---

### 6  Rolling once, rolling deep

`Roller` (a trait) owns two lazy values:

* `rows: ArrayBuffer[Roller]` – children to pick from.
* `shuffle: Shuffle` – helper that pre‑computes a cumulative weight list so weighted choice is O(log n) rather than re‑adding numbers every time.

`Table.roll` starts at the root table, calls `rollOne`, dives as long as the current node has children, and collects the visited strings. The result is a flat `Seq[String]` representing one complete path through the tree—imagine rolling on a table, hitting a subtable, then another subtable, until you bottom out.

---

### 7  String evaluation: dice and brace magic

`Eval`, mixed into `Tables.Ev`, walks each generated string character by character. It recognizes:

* `{ ... }` – recurse back into `Tables.apply` to resolve a table reference.
* `[XdY]` – roll X Y‑sided dice, defaulting to 1dY.

The evaluator is delightfully tiny: a four‑state loop (Plain, Evaluated, DiceNum, DiceSides) and two helper methods (`evaluate` and `rollDice`) delegated back to the enclosing service for RNG. Because it’s functional‑ish (no global state), you can swap in a different implementation—e.g. cryptographic RNG—without editing the core.

---

### 8  Words: n‑gram inventiveness

Sometimes you just need a brand‑new goblin name. That’s where `models.words` shines. `Associator` builds a directed graph keyed by *n‑grams up to length N* (default 3). Each edge counts how often fragment A is followed by fragment B in the source list. `roll()` grows a word by repeatedly:

1. Look at last maxN chars generated so far.
2. Gather every continuation ever seen after any suffix of that string.
3. Weighted‑pick the next chunk.
4. Append.

`uniq()` loops until it invents a word that isn’t already in the source list. With larger corpora—say, 40 000 English words—it invents plausible nonsense like “flarious” or “gribblet”.

---

### 9  Tables and words as services

Two short servlets wrap the core:

* **`TablesServlet`**

    * `GET /tables` → list table names.
    * `GET /tables/roll/{name}` → JSON `{"rolled":"...text..."}`.

* **`WordsServlet`**

    * `GET /words` → list word‑set names.
    * `GET /words/roll/{name}` → JSON with the generated word.

The code path is straight: parse resources once (lazy `val`) at startup, stash in memory, answer requests in <1 ms. No database, no filesystem writes, so horizontal scaling is trivial.

---

### 10  Build, run, test

Run `sbt run`, open `http://localhost:8888`, and you see whatever is in `public/index.html`. The server logs list of parse hits, so you can watch which tables load. `sbt test` runs ScalaTest suites in `/src/main/test`, mainly sanity checks for evaluation and parsing edge cases.

Because Jetty is embedded, packaging to a single JAR with `sbt assembly` works out of the box. You can then `java -jar fivel.jar` on any host with JRE 8+.

---

### 11  Unused but promising: H2 dependency

`build.sbt` bundles `com.h2database:h2:1.4.200`, yet the code never calls it. That smells like ambition: maybe the author planned a persistence layer for user‑defined tables or generated content. Keep that thought—we’ll exploit it in the roadmap.

---

### 12  Scrape folder: raw story corpus

Under `/scrape/stories` sits a mother‑lode of 2 000+ plain‑text files—everything from “Abandoned Candy Factory” to “Zeppelin Dreams”. Right now the folder is dead code: nothing reads it. But it’s a perfect seed pool for:

* bulk n‑gram models;
* semantic embedding training;
* style transfer experiments.

Treat it as free fuel.

---

### 13  What’s cool already

* **Scala 3** – pattern‑matching enums, given‑imports, minor ceremony.
* **Pure JVM** – zero native deps, trivial Dockerization.
* **Classpath resource loading** – local or shaded JAR, no difference.
* **Pluggable RNG** – pass a `Random` with seed for deterministic runs.
* **Tiny surface area** – <40 source files; onboarding time is an afternoon.

That’s a strong foundation for small teams or hobby tinkering.

---

### 14  Rough edges and quick wins

* No **error  handling** beyond `println`; malformed table breaks parsing silently. 
* Evaluator only nests once—`{ {foo} }` explodes. 
* `StaticContentServlet` doesn’t gzip or set cache headers. 
* No **unit tests** for parser edge cases such as mixed tabs/spaces. 
* Random selection inside `Associator.choices` re‑allocates maps each call; could cache. 
* Dependency versions frozen at 2021; Jetty 12 and Jackson 2.17 exist now. 

Low‑hanging fruit: fix logs, add Scala 3 ‑source‑compat flag, modernize libs.

---

### 15  Medium‑term extension ideas

1. **Live table editor**

    * React front‑end hitting `/tables`, JSON PUT to create/update.
    * Save to H2; watcher reloads changed tables without restart.

2. **OpenAPI spec + client SDKs**

    * Annotate servlets, auto‑generate docs at `/swagger`.
    * Publish JS/TypeScript client for quick integration in browser games.

3. **Dice syntax upgrade**

    * Support exploding dice (`4d6!`), drop‑lowest (`5d6d1`), percentile (`d%`).
    * Parse once into AST; evaluator becomes a visitor, making further math simple.

4. **Theme packs**

    * Bundle tables into versioned JARs: `fivel-table-fantasy`, `fivel-table-cyberpunk`.
    * `Tables.load` takes multiple classloaders; devs mix and match at runtime.

5. **Streaming mode**

    * Jetty’s `AsyncContext` streams chunks as soon as they’re rolled—nice for very large narrations.

6. **Auth layer**

    * Basic token check before write endpoints; keeps trolls out of your hosted demo.

---

### 16  Big‑vision leaps

**A. Interactive fiction engine**
Imagine a loop: client requests a *scene* table, gets text back, user picks an option, posts choice, server stitches the next scene. Because tables can reference previous rolls (`{lastChoice}` placeholder concept), you can simulate branching stories without heavy state machines.

**B. Markov + vector semantics**
Run every story in the `scrape` folder through a Sentence‑BERT embedder, cluster by topic, then auto‑build new tables whose rows are lines drawn from clusters. Now your table generator “knows” something like *haunted factory* vs *romantic zeppelin* themes.

**C. LLM turbo‑boost**
Keep fivel as deterministic scaffold but hand off final polish to a foundation model. Example flow:

1. Fivel rolls *“The River Spear of Bones”*.
2. Prompt GPT‑4o: *“Give me a 20‑word flavor text for a fantasy artifact called ‘The River Spear of Bones’.”*
3. Return JSON with both the raw roll and model‑polished blurb.

This blends fast local randomness with cloud‑scale creativity.

**D. Multiplayer “tableverse”**
Store user‑subscribed table packs in H2, replicate via WebSocket, let players trade generators like Pokémon cards. When one user adds “Arcane Elevator Mishaps”, everyone sees it live.

---

### 17  Deployment game plan

* **Dockerfile**: `FROM eclipse-temurin:21-jre`, copy shaded JAR, expose 8888, healthcheck `/hello`.
* **Kubernetes**: HorizontalPodAutoscaler keyed to CPU; memory footprint is tiny (\~120 MB heap cold).
* **CI**: GitHub Actions running `sbt +test`, `sbt assembly`, container build, push to GHCR.
* **CD**: ArgoCD or simple helm upgrade; configmaps mount extra table packs.

---

### 18  Why use H2 now?

Implement `/tables/save` that accepts multipart form (table file) and writes to `USER_TABLES` table with columns `(id, name, text, created_at)`. On startup, load classpath tables *plus* DB rows. That gives:

* persistence across restarts;
* version history (add `revision INT`);
* per‑user namespaces if you later add auth.

H2 memory mode for tests, file mode in prod, or swap to Postgres with zero code change thanks to JDBC.

---

### 19  Security checklist before exposing to the wild

* Validate table names (`^[a‑z0‑9 _-]{1,64}$`) to avoid directory traversal inside `{|../../etc/passwd}`.
* Cap dice sides and count (e.g., max 1000) or malicious user crashes CPU.
* Add `X‑Content‑Type‑Options: nosniff` and friends on static responses.
* Rate‑limit write endpoints; Jetty already has `QoSFilter`.

---

### 20  Testing strategy moving forward

* **Property tests** for parser: random valid/invalid indent combos, expect round‑trip.
* **Golden tests**: snapshot seventy common table rolls, check they’re stable under fixed seed.
* **Load tests**: Gatling script hitting `/tables/roll/plot` at 500 req/s; monitor p99 latency.
* **Fuzz** the evaluator with weird unicode or unmatched braces, confirm no infinite loop.

Add Gradle or Mill later if multi‑module.

---

### 21  Documentation facelift

Right now README says *“sbt run, open 8888”*. You can do better:

* Architecture diagram (one box per package, arrows).
* Example curl commands.
* Contribution guide: style, commit hooks (`scalafmt`, `scalafix`).
* Links to live demo or GitHub Pages compiled docs via `sbt‑site`.

Docs are cheap, goodwill is priceless.

---

### 22  Possible future file formats

* **YAML** – indentation already matches; roll weights become `count:` fields.
* **CSV** – good for Excel users; treat row index as weight.
* **TOML / JSON5** – configuration for editor tooling; IDE autocompletion.
* **Graphviz DOT** – visualize nested tables; SVG rendered in UI for non‑coders.

Each format adapter converts to the internal `Table` objects, so generation code stays unchanged.

---

### 23  UX niceties for a web front‑end

* Sticky sidebar listing all tables; click rolls instantly via fetch.
* Inline dice roller widget; exposes `[ ]` syntax learn‑by‑example.
* Dark mode because people code at night.
* “Copy to clipboard” button with toast message.
* Seed input so users can reproduce results (the random seed is part of query params).
* LocalStorage remember last 10 rolls.

Front‑end can be static assets under `/public`, so no extra backend glue.

---

### 24  Stretch: plugin API

Define a trait `Plugin` with hooks:

```scala
trait Plugin:
  def tables: Seq[(String, Table)]
  def words:  Seq[(String, Associator)]
  def onRequest(path: String, query: Map[String,String]): Option[String]
```

Drop JARs in `/plugins`, reflect‑load at startup. Users write their own generators in pure Scala, jar them, copy to folder, restart—instant new endpoints without editing core.

---

### 25  Performance notes

* Parsing happens once; after that, rolling is CPU‑bound string concatenation—*very* fast.
* Word associator building is O(totalChars²); fine for lists \~50 000 entries, but pre‑generate and cache if you grow bigger.
* Memory: each `Row` keeps its string; average row length 30 chars, a 10 000‑row corpus is <1 MB raw plus overhead.
* Dice rolls are trivial; avoid BigInteger unless you support million‑sided dice.

---

### 26  Conclusion (and next steps for you)

Fivel already gives hobby GMs, narrative designers, and coding tinkerers a pocket‑sized lab for emergent text. You can fork today, rename packages, drop in your own `.txt` assets, and deploy in under an hour.

If you want to push boundaries:

1. **Ship a browser UI** so non‑coders can click and grin.
2. **Persist user‑generated tables** via the unused H2 jar.
3. **Extend dice grammar** for crunchy tabletop fans.
4. **Blend with embeddings or LLMs** for flavour and coherence jumps.
5. **Encourage a community**—plugin API, table packs, showcase site.

Do those and fivel graduates from a neat code sample to a living ecosystem. Happy rolling!
