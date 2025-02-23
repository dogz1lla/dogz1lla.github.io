# Making a personal website with clojure
In this article I will briefly describe how I went about creating my own
website using clojure programming language.

To help me create and publish posts I used emacs' org-mode.

Please note that 
* I made a static website so no `clojurescript` shenanigans necessary;
* this article won't touch upon the deployment of the resulting web application
, I will share my experience with that in another article!

## Setting up your own (static) website using clojure
### Why clojure?
[Clojure](https://clojure.org) is a modern functional programming language that
happens to be a lisp.
It is an absolute pleasure to work with!
If you have some spare time and you would not mind spending it learning a lean,
elegant and practical programming language I highly recommend giving clojure a
shot. 

### `lein` or `deps.edn`?
Now there are at least two ways to go about building clojure projects.

One of them is to use [leiningen](https://leiningen.org) which has been a
de-facto building tool for clojure for quite some time.
Some consider its capabilities to be a bit too excessive in volume if not
overwhelming and I am one of those people.
So I personally went with an alternative which is
[clojure CLI](https://clojure.org/reference/deps_and_cli), to me it seems like
a leaner and more transparent choice. 

### Clojure libs
Which clojure libraries do we need to create a website?

Well, since the website is going to be static and overall very basic we won't
need to use `clojurescript` and can get away with plain clojure code.

Two main parts required are:
* _web server and routing_: this is taken care of by using [ring](https://github.com/ring-clojure/ring) / [compojure](https://github.com/weavejester/compojure) combo;
* _static web page generation_: a very common choice for this is [hiccup](https://github.com/weavejester/hiccup). N
ice thing about `hiccup` is that it allows one to create html using clojure dat
a structures which opens up all of the power of clojure to use for composing
the html tree.

Above I mentioned that I used clojure CLI for this project and all of the
configuration in this tool is done using a special file with the name `deps.edn`
that contains plain clojure data structures.
One specifies among other things the dependencies of the project in `deps.edn`
and for our simple website it looks like the following:

```clojure
{:paths ["src" "resources"]
 :deps
 {org.clojure/clojure     {:mvn/version "1.10.2"}
  compojure/compojure     {:mvn/version "1.6.1"}
  ring/ring-defaults      {:mvn/version "0.3.2"}
  ring/ring-jetty-adapter {:mvn/version "1.7.1"}}}
```

You might have noticed the key `:paths` in the example above, the corresponding
value contains a list of strings that specify folders containing code/files
relevant to building the project.
In our example we need to specify the folder that contains all of the source
code (`src`) and the folder that contains any static files (i.e., `.html` or
`.css` or others, `resources`)

## Generating static html documents using emacs org mode
Let me get this straight: `hiccup` is a great tool.
It was especially useful when generating the overall style of the website: I
was able to wrap `hiccup` code snippets into a clojure function and call it
every time I generate a new "view" (effectively on every new webpage).

However, writing bigger text documents such as this article that contain
prose/code snippets is a task I'd like to do in a proper text editor.

`Emacs` and its amazing `org-mode` to the rescue!

[Org mode](https://orgmode.org) is, in my humble opinion, is the biggest reason 
to use `emacs` (GNU text editor).

What is `org-mode`?

It is a markdown language that allows one to create beatifully formatted text 
documents with little effort.
It also has in-built facilities to export the documents into plain text/pdf/html
and other formats.
I used `org-mode` to write up this article and then exported it to an html
document using `Ctrl-c Ctrl-e Ctrl-b h h` button combination.

Notice the `Ctrl-b` bit -- it is important because it allows to export only the
"body" of the document, i.e., disregarding the `<html>` and `<head>` tags.
I don't want to have those because that is html that is common to all of the
webpages on my website and I would like to generate that in clojure using
`hiccup`. 

One note here: org documents allow one to tweak the export behaviour using
special headers.
I found it useful to include the following in the begining of my org article
document:
```bash
#+TITLE: The title of your article goes here!
#+OPTIONS: toc:nil html-head-include-default-style:nil num:nil
```

Notice the `OPTIONS` key.

Corresponding values do the following:
- `toc:nil` disables the table of contents;
- `html-head-include-default-style:nil` tells org to disregard the defaul css
org uses when exporting documents to html (I would like to use my own style
sheet!);
- `num:nil` turns off the numbering of the headlines (matter of personal
preference!);

## Letting clojure know about the article document
Now we have a clojure web server that is responsible for creating templates for
the webpages and reading css file among other things and we have html document
that contains the article text.
To make them work together I created a simple view function
```clojure
(defn article-page
  " Create an html page for the blog post that has been
  written in org mode and exported to the html file
  `filepath`."
  [filepath]
  (hiccup.page/html5
    ;; add code to generate the head of the web page here
    [:body
      (slurp filepath)]))
```

As you can see, adding the blog post is simply letting clojure slurp the html
document!

## Running the server using clojure CLI
When everything is ready it is time to do a test run locally.
There has to be an entry point to the app, which simply means a function that
starts a `ring` web server: 
```clojure
(defn -main
  [& args]
  (ring.adapter.jetty/run-jetty app-routes {:port 3000}))
```

where `app-routes` is definition of the routing you would like to have that is
created using `compojure`'s `defroutes` macro.

Now `cd` into your project directory and execute in the terminal 
```bash
clj -X your_project_name.main_clj_file_name/-main
```
and the server will start.
You should be able to see your webapp if you go to `http://localhost:3000/` in
your browser!
