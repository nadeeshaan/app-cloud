//Select2 functionality initialization
function initSelect2(data, element, prev) {
    var $select = $(element).select2({
        placeholder: "Value",
        data: data,
        multiple: true,
        dropdownAutoWidth: true,
        containerCssClass: 'custom-env-class-for-demo select',
        width: '350px',
        tags: true,
        selectOnBlur: true,
        createSearchChoice: function(term, data) {
            if ($(data).filter(function() {
                    return this.text.localeCompare(term) === 0;
                }).length === 0) {
                return {
                    id: term,
                    text: term
                };
            }
        },
    });

    $select.on("select2:select", function(e) {
        var l = $select.select2('data');
        e.params.data.text = e.params.data.id;
        $select.val(e.params.data.id).trigger('change');
        if (e.params.data.isNew != undefined && e.params.data.isNew) {
            $select.empty();
            $select.select2("val", "Invalid Selection");
        } else if (e.params.data.isNextLevel) {
            $select.empty();
            $select.trigger('change');
            $select = initSelect2(e.params.data.data, element);
            $select.select2('open');
        }
    });

    $select.on("select2:close", function(e) {
        var highlighted = $(e.target).data('select2').$dropdown.find('.select2-results__option--highlighted');
        if (highlighted) {
            var data = highlighted.data('data');
            var id = data.id;
            var display = data.name;
            if (id != 0) {
                $select.select2("val", id);
            }
        }
    });

    $(".select2-search__field").keyup(function(e) {
        if ($(this).val() == "database:") {
            var dbs;
            jagg.post("../blocks/database/list/ajax/list.jag", {
                action: "getAllDatabasesInfo"
            }, function(result) {
                dbs = JSON.parse(result);
                $select.trigger('change');
                $select = initSelect2(dbs, element);
                $select.select2('open');
            });
        }
    });

    //noinspection JSAnnotator
    return $select;
}