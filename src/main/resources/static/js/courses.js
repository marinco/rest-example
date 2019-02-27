$(document).ready(function () {

    var coursesTable = $("#courses_table");

    var addCourseToTable = function(course) {
        coursesTable.append(
            '<tr data-courseId=' + course.id + '>' +
            '<td>' +
            '<span class="innoedit name">' + course.name + '</span>' +
            '<input class="inedit name"/>' +
            '</td>' +
            '<td>' +
            '<span class="innoedit description">' + course.description + '</span>' +
            '<input class="inedit description"/>' +
            '</td>' +
            '<td>' +
            ( course.teacher == null ?
                    '<span class="innoedit teacher" data-teacherId=-1>No Teacher</span>':
                    '<span class="innoedit teacher" data-teacherId=' + course.teacher.id + '>' + course.teacher.firstName + ' ' + course.teacher.lastName + '</span>'
            ) +
            '<select class="inedit teacher -dropdownmenu"></select>' + //TODO
            '</td>' +
            '<td>' +
            '  <button class="btn btn-sm btn-warning edit innoedit">Edit</button>' +
            '  <button class="btn btn-sm btn-danger delete innoedit">Delete</button>' +
            '  <button class="btn btn-sm btn-danger update inedit">Update</button>' +
            '  <button class="btn btn-sm btn-success cancel inedit">Cancel</button>' +
            '</td>' +
            '</tr>'
        );
    };

    var addTeacherToStuff = function (tag, person, selected) {
        tag.append(
            '<option value =' + person.id + (person.id == selected ? ' selected' : "") + ' >' + person.firstName + ' ' + person.lastName + '</option>'
        );
    };

    $.ajax({
        type: 'GET',
        url: '/courses',
        success: function(courses) {
            $.each(courses, function(i, course) {
                addCourseToTable(course);
            });
        },
        error: function() {
            alert("Error loading courses!");
        }
    });

    coursesTable.delegate("button.delete", "click", function() {
        row = $(this).closest("tr");
        $.ajax({
            type: 'DELETE',
            url: "courses/" + $(row).attr('data-courseId'),
            success: function() {
                row.remove();
            },
            error: function() {
                alert("Can not delete row!");
            }
        });
    });

    coursesTable.delegate("button.edit", "click", function() {
        row = $(this).closest("tr");

        // copy from span to input
        row.find('input.name').val( row.find('span.name').html() );
        row.find('input.description').val( row.find('span.description').html() );

        var selectTeacher = $(row).find('select.teacher');
        var teacherId = $($(row).find('span.teacher')).attr('data-teacherId');
        selectTeacher.html("");

        $.ajax({
            type: 'GET',
            url: '/persons',
            success: function(persons) {
                $.each(persons, function(i, person) {
                    addTeacherToStuff(selectTeacher, person, teacherId)

                });
                addTeacherToStuff(selectTeacher, {id:"-1", firstName:"No", lastName:"Teacher"}, teacherId);
            },
            error: function() {
                alert("Error loading teachers!");
            }
        });

        row.addClass("inedit");
    });

    coursesTable.delegate("button.cancel", "click", function() {
        $(this).closest("tr").removeClass('inedit');
    });

    coursesTable.delegate("button.update", "click", function() {
        row = $(this).closest("tr");

        // get input values
        var name = row.find('input.name').val();
        var description= row.find('input.description').val();
        var courseData = {
            name: name,
            description: description
        };

        $.ajax({
            type: 'PUT',
            url: "courses/" + $(row).attr('data-courseId'),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(courseData),
            success: function() {
                row.find('span.name').html(name);
                row.find('span.description').html(description);
            },
            error: function() {
                alert("Can not update course information!");
            }

        });

        var teacherId = row.find('select.teacher').val();
        var spanTeacher = row.find('span.teacher');

        if ( teacherId == -1 ){
            $.ajax({
                type: 'DELETE',
                url: "courses/" + $(row).attr('data-courseId') + '/teacher',
                success: function() {
                    spanTeacher.html("No Teacher");
                    spanTeacher[0].setAttribute('data-teacherId', -1);
                },
                error: function() {
                    alert("Can not change teacher!");
                }

            });
        } else {
            $.ajax({
                type: 'PUT',
                url: "courses/" + $(row).attr('data-courseId') + '/teacher',
                contentType: "application/json; charset=utf-8",
                dataType: "text",
                data: "/" + teacherId,
                success: function () {
                    spanTeacher.html(row.find($('select.teacher option[value=' + teacherId +']')));
                    spanTeacher[0].setAttribute('data-teacherid', teacherId);
                },
                error: function() {
                    alert("Can not change teacher!");
                }
            });
        }

        row.removeClass("inedit");
    });


    $("#createNewCourseBtn").on("click", function() {
        var formCourseTeacher = $("#formCourseTeacher");

        formCourseTeacher.html("");
        $.ajax({
            type: 'GET',
            url: '/persons',
            success: function(persons) {
                $.each(persons, function(i, person) {
                    addTeacherToStuff(formCourseTeacher, person, "-1");
                });
                addTeacherToStuff(formCourseTeacher, {id:"-1", firstName:"No", lastName:"Teacher"}, -1);
            },
            error: function() {
                alert("Error loading teachers!");
            }
        });
    })

    $("#saveNewCourseBtn").on("click", function() {
        var name = $("#formCourseName").val();
        var description = $("#formCourseDescription").val();
        var courseData = {
            name: name,
            description: description
        };


        $.ajax({
            type: 'POST',
            url: '/courses',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(courseData),
            success: function(course) {
                addCourseToTable(course);
            },
            error: function() {
                alert("Can not create new course!");
            }
        });
        $("#newCourseModal").modal('hide');
    });

});