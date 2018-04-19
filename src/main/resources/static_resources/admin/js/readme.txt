js官网的有问题，是从demo里找的



标签这里IE判断bug已修改
 if ($.support.leadingWhitespace) { // for ie8
            $element.on('propertychange', $.proxy(this.tools.query, this))
        } else {
            $element.on('input', $.proxy(this.tools.query, this))
        }

