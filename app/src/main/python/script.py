from googletrans import Translator

def main(textt,lang_code):

    translator = Translator()

    # textt='Tumhare bina abb mann nahi lata kya tum'
    translations = translator.translate(textt, dest=lang_code)
    return translations.text

def mainn(textt):

    translator = Translator()

    # textt='Tumhare bina abb mann nahi lata kya tum'
    translations = translator.translate(textt)
    return translations.src
