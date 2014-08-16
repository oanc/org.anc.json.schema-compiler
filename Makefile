VERSION=$(shell cat VERSION)
NAME=jsonc
JAR=target/$(NAME)-$(VERSION).jar

help:
	@echo
	@echo "Available goals are:"
	@echo
	@echo "    clean : Clean removes all artifacts from previous builds"
	@echo "      jar : Creates the vocab.jar file."
	@echo "  install : Copies the jar to the user's bin directory."
	@echo "  release : Zips executables and uploads to the ANC web server."
	@echo "     help : Displays this help message."
	@echo
	
jar:
	mvn package
	
clean:
	mvn clean
	
install:
	#cp target/lsd-$(VERSION).jar $(HOME)/bin
	cp $(JAR) $(HOME)/bin
	cat src/test/resources/$(NAME) | sed 's/__VERSION__/$(VERSION)/' > $(HOME)/bin/$(NAME)
	
debug:
	@echo "Current version is $(VERSION)"
	
release:
	#mvn clean package
	if [ ! -f $(JAR) ] ; then mvn clean package ; fi
	cat src/test/resources/$(NAME) | sed 's/__VERSION__/$(VERSION)/' > target/$(NAME)
	cd target ; zip $(NAME)-$(VERSION).zip $(NAME)-$(VERSION).jar $(NAME) ; cp $(NAME)-$(VERSION).zip $(NAME)-latest.zip
	scp -P 22022 target/$(NAME)-$(VERSION).zip suderman@anc.org:/home/www/anc/downloads
	scp -P 22022 target/$(NAME)-latest.zip suderman@anc.org:/home/www/anc/downloads
	echo "Release complete."

